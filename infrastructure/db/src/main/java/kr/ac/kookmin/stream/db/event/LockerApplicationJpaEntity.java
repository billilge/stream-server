package kr.ac.kookmin.stream.db.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import kr.ac.kookmin.stream.db.common.BaseCreatedTimeEntity;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplication;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
// uk_locker_applications_active_member_key·uk_locker_applications_active_locker_key(V26)는 generated column
// active_member_key·active_locker_key에 걸려 있다. Entity에 매핑된 컬럼이 아니라 여기서는 선언하지 않는다.
@Table(
    name = "locker_applications",
    indexes = {
        @Index(name = "idx_locker_applications_member_id_applied_at", columnList = "member_id, applied_at"),
        @Index(
            name = "idx_locker_applications_locker_period_id_locker_id_is_canceled",
            columnList = "locker_period_id, locker_id, is_canceled"
        )
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LockerApplicationJpaEntity extends BaseCreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "locker_application_id")
    private Long id;

    @Column(name = "locker_period_id", nullable = false)
    private Long lockerPeriodId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "locker_id", nullable = false)
    private Long lockerId;

    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt;

    private LockerApplicationJpaEntity(LockerApplication application) {
        this.id = application.getId();
        this.lockerPeriodId = application.getLockerPeriodId();
        this.memberId = application.getMemberId();
        this.lockerId = application.getLockerId();
        this.appliedAt = application.getAppliedAt();
    }

    public static LockerApplicationJpaEntity from(LockerApplication application) {
        return new LockerApplicationJpaEntity(application);
    }

    public LockerApplication toDomain() {
        return LockerApplication.of(id, lockerPeriodId, memberId, lockerId, appliedAt);
    }
}
