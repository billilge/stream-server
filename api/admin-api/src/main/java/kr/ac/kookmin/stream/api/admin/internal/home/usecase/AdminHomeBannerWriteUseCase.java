package kr.ac.kookmin.stream.api.admin.internal.home.usecase;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.service.EventService;
import kr.ac.kookmin.stream.file.domain.FileErrorCode;
import kr.ac.kookmin.stream.file.service.FileService;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerCreateCommand;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerLandingType;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerUpdateCommand;
import kr.ac.kookmin.stream.internal.domain.home.service.AdminHomeService;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeErrorCode;
import kr.ac.kookmin.stream.welfare.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 배너를 쓰기 전에 연결할 이미지 파일과 랜딩 대상이 실제로 있는지 확인한다. 홈(internal)은 파일(file)·
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

    private final AdminHomeService adminHomeService;
    private final FileService fileService;
    private final NoticeService noticeService;
    private final EventService eventService;

    public void create(HomeBannerCreateCommand command) {
        validateImageExists(command.imageFileId());
        validateLandingTargetExists(command.landingType(), command.targetId());
        adminHomeService.createBanner(command);
    }

    public void update(Long bannerId, HomeBannerUpdateCommand command) {
        validateImageExists(command.imageFileId());
        validateLandingTargetExists(command.landingType(), command.targetId());
        adminHomeService.updateBanner(bannerId, command);
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
