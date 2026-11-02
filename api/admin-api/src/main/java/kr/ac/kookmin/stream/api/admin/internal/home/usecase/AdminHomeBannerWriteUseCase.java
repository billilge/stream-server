package kr.ac.kookmin.stream.api.admin.internal.home.usecase;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.service.EventService;
import kr.ac.kookmin.stream.file.domain.FileErrorCode;
import kr.ac.kookmin.stream.file.service.FileService;
import kr.ac.kookmin.stream.internal.domain.config.service.AdminConfigValueService;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerCategory;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerCreateCommand;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerLandingType;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerUpdateCommand;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeErrorCode;
import kr.ac.kookmin.stream.internal.domain.home.service.AdminHomeService;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeErrorCode;
import kr.ac.kookmin.stream.welfare.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 배너를 쓰기 전에 배경 이미지를 정하고, 연결할 랜딩 대상이 실제로 있는지 확인한다. 홈(internal)은 파일(file)·
 * 공지(welfare)·행사(event)를 모르므로 도메인끼리 직접 의존하지 않고 이 UseCase가 조합한다
 * ({@code architecture.md} 6-1절).
 * <p>
 * 확인과 저장을 한 트랜잭션으로 묶지 않는다. 확인 직후 대상이 지워져도 홈은 짝이 깨진 배너를 빼고,
 * 상세는 연결 대상만 비워 내려주므로 원자성이 필요하지 않다.
 * <p>
 * 아직 공개하지 않은 행사도 연결할 수 있다. 운영진이 공개 예정 행사의 배너를 미리 만들어 두기 때문이다.
 */
@Component
@RequiredArgsConstructor
public class AdminHomeBannerWriteUseCase {

    /**
     * 카테고리별 프리셋 배경의 파일 식별자를 담아 둔 설정 키의 앞부분. 뒤에 카테고리 이름이 붙는다.
     * <p>
     * 파일 식별자는 환경마다 달라 코드에 둘 수 없다. 배포할 때 {@code admin_config_values}에 손으로 넣는다.
     */
    private static final String PRESET_IMAGE_KEY_PREFIX = "home.banner.preset.";

    private final AdminHomeService adminHomeService;
    private final AdminConfigValueService adminConfigValueService;
    private final FileService fileService;
    private final NoticeService noticeService;
    private final EventService eventService;

    public void create(HomeBannerCreateCommand command) {
        Long imageFileId = resolveImageFileId(command.category(), command.imageFileId());
        validateLandingTargetExists(command.landingType(), command.targetId());

        adminHomeService.createBanner(command.withImageFileId(imageFileId));
    }

    public void update(Long bannerId, HomeBannerUpdateCommand command) {
        Long imageFileId = resolveImageFileId(command.category(), command.imageFileId());
        validateLandingTargetExists(command.landingType(), command.targetId());

        adminHomeService.updateBanner(bannerId, command.withImageFileId(imageFileId));
    }

    /**
     * 저장할 배경 이미지를 정한다. {@code ETC}만 운영진이 올린 파일을 쓰고, 나머지는 카테고리의 프리셋으로
     * 덮는다 — 프리셋 카테고리에 파일을 함께 보내도 무시한다. 랜딩 타입이 읽지 않는 값을 비워 저장하는 것과
     * 같은 처리다.
     */
    private Long resolveImageFileId(HomeBannerCategory category, Long uploadedFileId) {
        if (category != HomeBannerCategory.ETC) {
            return presetImageFileId(category);
        }
        if (uploadedFileId == null) {
            throw new BusinessException(HomeErrorCode.HOME_BANNER_IMAGE_REQUIRED);
        }

        validateImageExists(uploadedFileId);
        return uploadedFileId;
    }

    /**
     * 카테고리의 프리셋 배경. 설정이 없거나, 숫자가 아니거나, 가리키는 파일이 사라졌으면 모두 설정이
     * 잘못된 것이라 같은 에러로 묶는다 — 운영진이 입력으로 풀 수 있는 문제가 아니다.
     */
    private Long presetImageFileId(HomeBannerCategory category) {
        Long presetFileId = adminConfigValueService.getValue(PRESET_IMAGE_KEY_PREFIX + category.name())
            .map(AdminHomeBannerWriteUseCase::parseFileIdOrNull)
            .orElseThrow(() -> new BusinessException(HomeErrorCode.HOME_BANNER_PRESET_NOT_CONFIGURED));

        if (presetFileId == null || fileService.findById(presetFileId).isEmpty()) {
            throw new BusinessException(HomeErrorCode.HOME_BANNER_PRESET_NOT_CONFIGURED);
        }
        return presetFileId;
    }

    // 설정값은 문자열이라 숫자가 아닐 수 있다. 던지지 않고 null로 돌려 호출부가 한 자리에서 처리한다
    private static Long parseFileIdOrNull(String configValue) {
        try {
            return Long.valueOf(configValue.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void validateImageExists(Long imageFileId) {
        if (fileService.findById(imageFileId).isEmpty()) {
            throw new BusinessException(FileErrorCode.FILE_NOT_FOUND);
        }
    }

    // 랜딩 타입을 switch로 모두 받는다. 값이 늘면 컴파일이 막으므로 새 타입의 존재 확인이 조용히 빠지지 않는다
    private void validateLandingTargetExists(HomeBannerLandingType landingType, Long targetId) {
        // 식별자가 비어 있는 건 랜딩 짝이 어긋난 것이라 도메인이 가린다. 여기서 null로 조회하지 않는다
        if (targetId == null) {
            return;
        }

        switch (landingType) {
            case NOTICE -> {
                if (noticeService.getByIdOrNull(targetId) == null) {
                    throw new BusinessException(NoticeErrorCode.NOTICE_NOT_FOUND);
                }
            }
            case EVENT -> {
                if (eventService.getByIdOrNull(targetId) == null) {
                    throw new BusinessException(EventErrorCode.EVENT_NOT_FOUND);
                }
            }
            case EXTERNAL_LINK -> {
                // 앱 밖으로 나가는 주소라 확인할 대상이 없다
            }
        }
    }
}
