package kr.ac.kookmin.stream.db.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.ac.kookmin.stream.db.common.BaseSoftDeleteEntity;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "lockers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LockerJpaEntity extends BaseSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "locker_id")
    private Long id;

    @Column(name = "locker_number", nullable = false, length = 20)
    private String lockerNumber;

    @Column(nullable = false, length = 20)
    private String section;

    @Column(name = "row_no", nullable = false)
    private int rowNo;

    @Column(name = "column_no", nullable = false)
    private int columnNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LockerStatus status;

    private LockerJpaEntity(Locker locker) {
        this.id = locker.getId();
        this.lockerNumber = locker.getLockerNumber();
        this.section = locker.getSection();
        this.rowNo = locker.getRowNo();
        this.columnNo = locker.getColumnNo();
        this.status = locker.getStatus();
    }

    public static LockerJpaEntity from(Locker locker) {
        return new LockerJpaEntity(locker);
    }

    public Locker toDomain() {
        return Locker.of(id, lockerNumber, section, rowNo, columnNo, status);
    }
}
