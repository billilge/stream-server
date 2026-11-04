package kr.ac.kookmin.stream.api.admin.internal.home.usecase;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.api.admin.internal.home.response.AdminHomeBannerDetailResponse;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.service.EventService;
import kr.ac.kookmin.stream.file.service.FileService;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBanner;
import kr.ac.kookmin.stream.internal.domain.home.service.AdminHomeService;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 배너 상세. 배너가 가리키는 공지·행사의 제목은 각 도메인이 갖고 있어 여기서 붙인다
 * ({@code architecture.md} 6-1절).
 */
@Component
@RequiredArgsConstructor
public class AdminHomeBannerDetailUseCase {

    private final AdminHomeService adminHomeService;
    private final FileService fileService;
    private final NoticeService noticeService;
    private final EventService eventService;

    public AdminHomeBannerDetailResponse getBanner(Long bannerId) {
        HomeBanner banner = adminHomeService.getBanner(bannerId);

        return AdminHomeBannerDetailResponse.of(
            banner,
            StorageUrlBuilder.build(fileService.findById(banner.getImageFileId()).orElse(null)),
            toLinkedTarget(banner),
            LocalDateTime.now()
        );
    }

    /**
     * 연결 대상이 지워졌으면 비워 둔다. 배너는 멀쩡하므로 상세까지 막지 않는다 —
     * 막으면 고쳐야 할 배너를 열지 못한다.
     * <p>
     * 아직 공개하지 않은 행사는 담는다. 운영진이 공개 예정 행사의 배너를 미리 만들어 두기 때문이다.
     * <p>
     * 랜딩 타입을 switch 식으로 모두 받는다. 값이 늘면 컴파일이 막으므로 새 타입이 조용히 빈 연결 대상으로
     * 떨어지지 않는다.
     */
    private AdminHomeBannerDetailResponse.LinkedTarget toLinkedTarget(HomeBanner banner) {
        return switch (banner.getLandingType()) {
            case NOTICE -> toLinkedNotice(banner.getTargetId());
            case EVENT -> toLinkedEvent(banner.getTargetId());
            // 앱 밖으로 나가는 주소라 연결할 대상이 없다. 주소는 배너의 linkUrl이 그대로 내려간다
            case EXTERNAL_LINK -> null;
        };
    }

    private AdminHomeBannerDetailResponse.LinkedTarget toLinkedNotice(Long targetId) {
        Notice notice = noticeService.getByIdOrNull(targetId);
        return notice == null ? null
            : new AdminHomeBannerDetailResponse.LinkedTarget(notice.getId(), notice.getTitle());
    }

    private AdminHomeBannerDetailResponse.LinkedTarget toLinkedEvent(Long targetId) {
        Event event = eventService.getByIdOrNull(targetId);
        return event == null ? null
            : new AdminHomeBannerDetailResponse.LinkedTarget(event.getId(), event.getTitle());
    }
}
