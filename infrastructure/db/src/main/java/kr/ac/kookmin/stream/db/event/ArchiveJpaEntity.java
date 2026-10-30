package kr.ac.kookmin.stream.db.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import kr.ac.kookmin.stream.db.common.BaseSoftDeleteEntity;
import kr.ac.kookmin.stream.event.domain.archive.domain.Archive;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

// 바뀐 컬럼만 UPDATE한다(@DynamicUpdate). 수정은 같은 트랜잭션에서 findById로 엔티티를 영속성 컨텍스트에 올린 뒤
// merge하므로, 도메인이 들고 있지 않은 is_deleted는 읽어 둔 값 그대로여서 변경으로 잡히지 않고 UPDATE에서 빠진다.
// 덕분에 운영진 A가 수정하는 사이 B가 삭제를 커밋해도 A의 UPDATE가 삭제를 되돌리지 않는다.
// (NoticeJpaEntity·LockerJpaEntity가 같은 이유로 붙이고 있다)
@Entity
@DynamicUpdate
@Table(name = "archives")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArchiveJpaEntity extends BaseSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "archive_id")
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    private String location;

    @Column(name = "department_name", nullable = false, length = 100)
    private String departmentName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "image_ids", columnDefinition = "json")
    private List<Long> imageIds;

    @Column(name = "is_exposed", nullable = false)
    private boolean exposed;

    @Column(name = "related_event_id")
    private Long relatedEventId;

    private ArchiveJpaEntity(Archive archive) {
        this.id = archive.getId();
        this.title = archive.getTitle();
        this.content = archive.getContent();
        this.startDate = archive.getStartDate();
        this.endDate = archive.getEndDate();
        this.location = archive.getLocation();
        this.departmentName = archive.getDepartmentName();
        this.imageIds = archive.getImageIds();
        this.exposed = archive.isExposed();
        this.relatedEventId = archive.getRelatedEventId();
    }

    public static ArchiveJpaEntity from(Archive archive) {
        return new ArchiveJpaEntity(archive);
    }

    public Archive toDomain() {
        return Archive.of(
            id, title, content, startDate, endDate, location, departmentName, imageIds, exposed,
            relatedEventId, getCreatedAt());
    }
}
