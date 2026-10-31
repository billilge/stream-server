package kr.ac.kookmin.stream.db.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.ac.kookmin.stream.db.common.BaseTimeEntity;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplySection;
import kr.ac.kookmin.stream.internal.domain.home.domain.HomeApplyTargetType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "home_apply_sections")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HomeApplySectionJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "home_apply_section_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private HomeApplyTargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "is_exposed", nullable = false)
    private boolean isExposed;

    public HomeApplySection toDomain() {
        return HomeApplySection.of(id, targetType, targetId, displayOrder, isExposed);
    }
}
