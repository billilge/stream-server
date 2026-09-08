package kr.ac.kookmin.stream.db.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import kr.ac.kookmin.stream.db.common.BaseCreatedTimeEntity;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplication;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "locker_applications",
    indexes = {
        @Index(name = "idx_locker_applications_locker_period_id_locker_id", columnList = "locker_period_id, locker_id"),
        @Index(name = "idx_locker_applications_member_id", columnList = "member_id")
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

    private LockerApplicationJpaEntity(LockerApplication application) {
        this.id = application.getId();
        this.lockerPeriodId = application.getLockerPeriodId();
        this.memberId = application.getMemberId();
        this.lockerId = application.getLockerId();
    }

    public static LockerApplicationJpaEntity from(LockerApplication application) {
        return new LockerApplicationJpaEntity(application);
    }

    public LockerApplication toDomain() {
        return LockerApplication.of(id, lockerPeriodId, memberId, lockerId);
    }
}
