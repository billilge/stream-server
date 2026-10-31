package kr.ac.kookmin.stream.api.admin.internal.home.response;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBanner;

/**
 * 노출 중 탭의 배너 목록. 순서를 드래그로 바꾸므로 페이지로 자르지 않고 전체를 표시 순서대로 담는다.
 */
public record AdminHomeBannerListResponse(List<AdminHomeBannerSummaryResponse> content) {

    public static AdminHomeBannerListResponse of(List<HomeBanner> banners, LocalDateTime now) {
        return new AdminHomeBannerListResponse(banners.stream()
            .map(banner -> AdminHomeBannerSummaryResponse.of(banner, now))
            .toList());
    }
}
