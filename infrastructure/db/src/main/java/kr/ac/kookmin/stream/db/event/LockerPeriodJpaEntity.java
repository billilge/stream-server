package kr.ac.kookmin.stream.db.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import kr.ac.kookmin.stream.db.common.BaseTimeEntity;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerPeriod;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "locker_periods")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LockerPeriodJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "locker_period_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "apply_start_at", nullable = false)
    private LocalDateTime applyStartAt;

    @Column(name = "apply_end_at", nullable = false)
    private LocalDateTime applyEndAt;

    @Column(name = "usage_start_at", nullable = false)
    private LocalDate usageStartAt;

    @Column(name = "usage_end_at", nullable = false)
    private LocalDate usageEndAt;

    private LockerPeriodJpaEntity(LockerPeriod period) {
        this.id = period.getId();
        this.name = period.getName();
        this.applyStartAt = period.getApplyStartAt();
        this.applyEndAt = period.getApplyEndAt();
        this.usageStartAt = period.getUsageStartAt();
        this.usageEndAt = period.getUsageEndAt();
    }

    public static LockerPeriodJpaEntity from(LockerPeriod period) {
        return new LockerPeriodJpaEntity(period);
    }

    public LockerPeriod toDomain() {
        return LockerPeriod.of(id, name, applyStartAt, applyEndAt, usageStartAt, usageEndAt);
    }
}
