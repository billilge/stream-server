package kr.ac.kookmin.stream.api.admin.internal.home.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBanner;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerLandingType;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerStatus;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerType;

/**
 * 배너 상세. 수정 화면의 초기값으로도 쓴다.
 *
 * @param linkedTarget 연결된 공지·행사. 외부 링크면 비어 있고, 대상이 지워졌거나 아직 공개 전이어도 비어 있다
 * @param linkUrl      외부 링크 주소. {@code EXTERNAL_LINK}일 때만 채워진다
 */
public record AdminHomeBannerDetailResponse(
    Long bannerId,
    HomeBannerType bannerType,
    HomeBannerStatus status,
    String title,
    String subtitle,
    String imageUrl,
    LocalDateTime exposureStartAt,
    LocalDateTime exposureEndAt,
    HomeBannerLandingType landingType,
    LinkedTarget linkedTarget,
    String linkUrl
) {

    public static AdminHomeBannerDetailResponse of(
        HomeBanner banner,
        String imageUrl,
        LinkedTarget linkedTarget,
        LocalDateTime now
    ) {
        return new AdminHomeBannerDetailResponse(
            banner.getId(),
            banner.getBannerType(),
            banner.calculateStatus(now),
            banner.getTitle(),
            banner.getSubtitle(),
            imageUrl,
            banner.getExposureStartAt(),
            banner.getExposureEndAt(),
            banner.getLandingType(),
            linkedTarget,
            banner.getLinkUrl()
        );
    }

    /**
     * 연결된 공지 또는 행사. 무엇인지는 배너의 {@code landingType}이 정하므로 여기서는 식별자와 제목만 담는다.
     */
    public record LinkedTarget(Long targetId, String title) {
    }
}
