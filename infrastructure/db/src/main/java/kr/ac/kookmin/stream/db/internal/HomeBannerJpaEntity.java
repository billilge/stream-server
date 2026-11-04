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
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeBannerLandingType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
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

    public HomeBanner toDomain() {
        return HomeBanner.of(
            id, title, subtitle, imageFileId, landingType, targetId, linkUrl,
            displayOrder, exposureStartAt, exposureEndAt
        );
    }
}
