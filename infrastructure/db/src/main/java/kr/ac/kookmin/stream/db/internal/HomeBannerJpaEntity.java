package kr.ac.kookmin.stream.db.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import kr.ac.kookmin.stream.db.common.BaseSoftDeleteEntity;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBanner;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerCategory;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerLandingType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
// 바뀐 컬럼만 UPDATE한다. 도메인이 isDeleted를 들고 있지 않아 from(banner)은 늘 is_deleted = false인 엔티티를
// 만드는데, 모든 컬럼을 담은 정적 UPDATE였다면 수정·순서 변경이 동시 삭제를 되돌려 버린다
@DynamicUpdate
@Table(name = "home_banners")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HomeBannerJpaEntity extends BaseSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "home_banner_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 100)
    private String subtitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private HomeBannerCategory category;

    @Column(name = "image_file_id", nullable = false)
    private Long imageFileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "landing_type", nullable = false, length = 20)
    private HomeBannerLandingType landingType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "link_url", length = 2048)
    private String linkUrl;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "exposure_start_at", nullable = false)
    private LocalDateTime exposureStartAt;

    @Column(name = "exposure_end_at", nullable = false)
    private LocalDateTime exposureEndAt;

    private HomeBannerJpaEntity(HomeBanner banner) {
        this.id = banner.getId();
        this.title = banner.getTitle();
        this.subtitle = banner.getSubtitle();
        this.category = banner.getCategory();
        this.imageFileId = banner.getImageFileId();
        this.landingType = banner.getLandingType();
        this.targetId = banner.getTargetId();
        this.linkUrl = banner.getLinkUrl();
        this.displayOrder = banner.getDisplayOrder();
        this.exposureStartAt = banner.getExposureStartAt();
        this.exposureEndAt = banner.getExposureEndAt();
    }

    public static HomeBannerJpaEntity from(HomeBanner banner) {
        return new HomeBannerJpaEntity(banner);
    }

    public HomeBanner toDomain() {
        return HomeBanner.of(
            id, title, subtitle, category, imageFileId, landingType, targetId, linkUrl,
            displayOrder, exposureStartAt, exposureEndAt, getCreatedAt()
        );
    }
}
