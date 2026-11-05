package kr.ac.kookmin.stream.api.admin.internal.home.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBanner;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerCategory;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerLandingType;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerStatus;

/**
 * 운영진 배너 목록의 한 행. 노출 중 탭과 지난 배너 탭이 같은 모양을 쓴다.
 */
public record AdminHomeBannerListItemResponse(
    Long bannerId,
    HomeBannerCategory category,
    String title,
    HomeBannerLandingType landingType,
    LocalDateTime exposureStartAt,
    LocalDateTime exposureEndAt,
    HomeBannerStatus status
) {

    /**
     * @param now 노출 상태를 판정할 기준 시각. 목록 전체가 같은 시각을 봐야 한 요청 안에서 상태가 엇갈리지 않는다
     */
    public static AdminHomeBannerListItemResponse of(HomeBanner banner, LocalDateTime now) {
        return new AdminHomeBannerListItemResponse(
            banner.getId(),
            banner.getCategory(),
            banner.getTitle(),
            banner.getLandingType(),
            banner.getExposureStartAt(),
            banner.getExposureEndAt(),
            banner.calculateStatus(now)
        );
    }
}
