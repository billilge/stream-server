package kr.ac.kookmin.stream.api.admin.internal.home.usecase;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.api.admin.internal.home.response.AdminHomeBannerDetailResponse;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.service.EventService;
import kr.ac.kookmin.stream.file.service.FileService;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBanner;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerLandingType;
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
     * 연결 대상이 지워졌거나 아직 공개 전이면 비워 둔다. 배너는 멀쩡하므로 상세까지 막지 않는다 —
     * 막으면 고쳐야 할 배너를 열지 못한다.
     */
    private AdminHomeBannerDetailResponse.LinkedTarget toLinkedTarget(HomeBanner banner) {
        if (banner.getLandingType() == HomeBannerLandingType.NOTICE) {
            Notice notice = noticeService.getByIdOrNull(banner.getTargetId());
            return notice == null ? null
                : new AdminHomeBannerDetailResponse.LinkedTarget(notice.getId(), notice.getTitle());
        }
        if (banner.getLandingType() == HomeBannerLandingType.EVENT) {
            Event event = eventService.getByIdOrNull(banner.getTargetId());
            return event == null ? null
                : new AdminHomeBannerDetailResponse.LinkedTarget(event.getId(), event.getTitle());
        }
        return null;
    }
}
