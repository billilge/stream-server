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

// 바뀐 컬럼만 UPDATE한다(@DynamicUpdate). 운영진 둘이 서로 다른 필드를 고칠 때 읽어 둔 나머지 컬럼까지 되쓰는 것을
// 막는다. 다만 삭제 표시(is_deleted)는 이것으로 지켜지지 않는다 — 도메인이 그 값을 들고 있지 않아 merge가 항상
// false를 싣고, 수정과 삭제가 겹치면 삭제가 되돌아갈 수 있다. 저장소의 다른 소프트 삭제 엔티티도 같은 상태다.
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

    /** 연결한 행사. 연결하지 않았으면 NULL이다. */
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
