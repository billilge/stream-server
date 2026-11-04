package kr.ac.kookmin.stream.api.admin.internal.home.usecase;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.ErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.service.EventService;
import kr.ac.kookmin.stream.file.domain.FileErrorCode;
import kr.ac.kookmin.stream.file.service.FileService;
import kr.ac.kookmin.stream.internal.domain.config.service.AdminConfigValueService;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBanner;
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
 * 확인과 저장을 한 트랜잭션으로 묶지 않는다. 대상은 등록한 뒤에도 언제든 지워질 수 있어 묶어도 같은
 * 상태가 생기고, 그때 상세는 연결 대상만 비워 내려준다.
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
        Long imageFileId = resolveUpdateImageFileId(bannerId, command);
        validateLandingTargetExists(command.landingType(), command.targetId());

        adminHomeService.updateBanner(bannerId, command.withImageFileId(imageFileId));
    }

    /**
     * 수정할 때 저장할 배경 이미지를 정한다. 등록과 다른 점은 하나뿐이다 — {@code ETC}인데 파일을 보내지
     * 않았으면 "이미지는 그대로 두고 나머지만 고친다"는 뜻으로 보고 기존 이미지를 유지한다. 수정은 입력값을
     * 통째로 바꾸는 방식이라 화면이 매번 같은 파일 식별자를 되돌려 보내야 하는데, 한 번 빠뜨리면 제목만
     * 고치려던 요청이 거부된다.
     * <p>
     * 유지하는 식별자는 새로 들어온 입력이 아니라 이미 저장돼 있던 값이므로 파일 존재 확인을 다시 하지 않는다.
     * 다시 보면 이미지 파일이 지워진 배너는 제목조차 고치지 못한다.
     * <p>
     * 다른 카테고리에서 {@code ETC}로 바꾸는 경우는 유지할 이미지가 없다 — 기존 값은 프리셋 파일을 가리키고
     * 있어 그대로 쓰면 운영진이 고르지 않은 배경이 붙는다. 등록과 같이 파일을 요구한다.
     */
    private Long resolveUpdateImageFileId(Long bannerId, HomeBannerUpdateCommand command) {
        if (command.category() != HomeBannerCategory.ETC || command.imageFileId() != null) {
            return resolveImageFileId(command.category(), command.imageFileId());
        }

        HomeBanner banner = adminHomeService.getBanner(bannerId);
        if (banner.getCategory() != HomeBannerCategory.ETC) {
            throw new BusinessException(HomeErrorCode.HOME_BANNER_IMAGE_REQUIRED);
        }
        return banner.getImageFileId();
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
     * 카테고리의 프리셋 배경. 설정이 없거나 가리키는 파일이 사라졌으면 모두 설정이 잘못된 것이라 같은
     * 에러로 묶는다 — 운영진이 입력으로 풀 수 있는 문제가 아니다.
     * <p>
     * 값이 숫자가 아니면 파싱에서 그대로 터진다. 배포할 때 손으로 넣는 값이라 회비 금액
     * ({@code FeeAccountProvider})·대여 차단 기간과 같은 선택이다.
     */
    private Long presetImageFileId(HomeBannerCategory category) {
        Long presetFileId = adminConfigValueService.getValue(PRESET_IMAGE_KEY_PREFIX + category.name())
            .map(String::trim)
            .map(Long::parseLong)
            .orElseThrow(() -> new BusinessException(HomeErrorCode.HOME_BANNER_PRESET_NOT_CONFIGURED));

        if (fileService.findById(presetFileId).isEmpty()) {
            throw new BusinessException(HomeErrorCode.HOME_BANNER_PRESET_NOT_CONFIGURED);
        }
        return presetFileId;
    }

    private void validateImageExists(Long imageFileId) {
        if (fileService.findById(imageFileId).isEmpty()) {
            throw new BusinessException(FileErrorCode.FILE_NOT_FOUND);
        }
    }

    // 랜딩 타입을 switch 식으로 받는다. 값을 돌려주는 식이라 타입이 늘면 컴파일이 막는다 — switch 문은
    // 화살표를 써도 빠진 case를 잡지 않아 새 타입의 존재 확인이 조용히 빠진다
    private void validateLandingTargetExists(HomeBannerLandingType landingType, Long targetId) {
        // 식별자가 비어 있는 건 랜딩 짝이 어긋난 것이라 도메인이 가린다. 여기서 null로 조회하지 않는다
        if (targetId == null) {
            return;
        }

        ErrorCode notFound = switch (landingType) {
            case NOTICE -> noticeService.getByIdOrNull(targetId) == null ? NoticeErrorCode.NOTICE_NOT_FOUND : null;
            case EVENT -> eventService.getByIdOrNull(targetId) == null ? EventErrorCode.EVENT_NOT_FOUND : null;
            // 앱 밖으로 나가는 주소라 확인할 대상이 없다
            case EXTERNAL_LINK -> null;
        };

        if (notFound != null) {
            throw new BusinessException(notFound);
        }
    }
}
