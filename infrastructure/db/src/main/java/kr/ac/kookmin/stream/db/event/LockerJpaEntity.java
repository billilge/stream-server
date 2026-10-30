package kr.ac.kookmin.stream.db.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import kr.ac.kookmin.stream.db.common.BaseSoftDeleteEntity;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(
    name = "lockers",
    indexes = @Index(
        name = "idx_lockers_section_id_is_deleted_row_no_column_no",
        columnList = "section_id, is_deleted, row_no, column_no"
    )
)
// 바뀐 컬럼만 UPDATE한다. 상태 변경은 도메인에서 만든 엔티티를 merge하는데, 그 엔티티는 삭제 여부를 들고 있지 않아
// is_deleted가 늘 false다. 사물함 삭제 기능이 붙은 뒤 수정과 삭제가 겹치면 읽어 둔 값으로 되써서 삭제가 되돌아간다
// (LockerPeriodJpaEntity와 같은 이유).
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LockerJpaEntity extends BaseSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "locker_id")
    private Long id;

    @Column(name = "section_id", nullable = false)
    private Long sectionId;

    @Column(name = "locker_label", nullable = false, length = 20)
    private String lockerLabel;

    @Column(name = "locker_number", nullable = false)
    private int lockerNumber;

    @Column(name = "row_no", nullable = false)
    private int rowNo;

    @Column(name = "column_no", nullable = false)
    private int columnNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LockerStatus status;

    private LockerJpaEntity(Locker locker) {
        this.id = locker.getId();
        this.sectionId = locker.getSectionId();
        this.lockerLabel = locker.getLockerLabel();
        this.lockerNumber = locker.getLockerNumber();
        this.rowNo = locker.getRowNo();
        this.columnNo = locker.getColumnNo();
        this.status = locker.getStatus();
    }

    public static LockerJpaEntity from(Locker locker) {
        return new LockerJpaEntity(locker);
    }

    public Locker toDomain() {
        return Locker.of(id, sectionId, lockerLabel, lockerNumber, rowNo, columnNo, status);
    }
}
