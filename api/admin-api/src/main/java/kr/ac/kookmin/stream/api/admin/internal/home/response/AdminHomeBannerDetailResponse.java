package kr.ac.kookmin.stream.api.admin.internal.home.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBanner;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerCategory;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerLandingType;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerStatus;

/**
 * 배너 상세. 수정 화면의 초기값으로도 쓴다.
 *
 * @param imageFileId  배경 이미지의 파일 식별자. 수정 화면이 이미지를 그대로 둘지 가리는 데 쓴다 — 프리셋
 *                     카테고리면 서버가 채운 프리셋 파일을 가리킨다
 * @param linkedTarget 연결된 공지·행사. 외부 링크면 비어 있고, 대상이 지워졌어도 비어 있다. 아직 공개하지 않은 행사는 담긴다
 * @param linkUrl      외부 링크 주소. {@code EXTERNAL_LINK}일 때만 채워진다
 */
public record AdminHomeBannerDetailResponse(
    Long bannerId,
    HomeBannerCategory category,
    HomeBannerStatus status,
    String title,
    String subtitle,
    Long imageFileId,
    String imageUrl,
    LocalDateTime exposureStartAt,
    LocalDateTime exposureEndAt,
    HomeBannerLandingType landingType,
    LinkedTarget linkedTarget,
    String linkUrl,
    LocalDateTime createdAt
) {

    public static AdminHomeBannerDetailResponse of(
        HomeBanner banner,
        String imageUrl,
        LinkedTarget linkedTarget,
        LocalDateTime now
    ) {
        return new AdminHomeBannerDetailResponse(
            banner.getId(),
            banner.getCategory(),
            banner.calculateStatus(now),
            banner.getTitle(),
            banner.getSubtitle(),
            banner.getImageFileId(),
            imageUrl,
            banner.getExposureStartAt(),
            banner.getExposureEndAt(),
            banner.getLandingType(),
            linkedTarget,
            banner.getLinkUrl(),
            banner.getCreatedAt()
        );
    }

    /**
     * 연결된 공지 또는 행사. 무엇인지는 배너의 {@code landingType}이 정하므로 여기서는 식별자와 제목만 담는다.
     */
    public record LinkedTarget(Long targetId, String title) {
    }
}
