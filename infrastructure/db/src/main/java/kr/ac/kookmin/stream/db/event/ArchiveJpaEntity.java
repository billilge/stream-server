package kr.ac.kookmin.stream.db.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.List;
import kr.ac.kookmin.stream.db.common.BaseTimeEntity;
import kr.ac.kookmin.stream.event.domain.archive.domain.Archive;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "archives")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArchiveJpaEntity extends BaseTimeEntity {

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

    private ArchiveJpaEntity(Archive archive) {
        this.id = archive.getId();
        this.title = archive.getTitle();
        this.content = archive.getContent();
        this.startDate = archive.getStartDate();
        this.endDate = archive.getEndDate();
        this.location = archive.getLocation();
        this.departmentName = archive.getDepartmentName();
        this.imageIds = archive.getImageIds();
    }

    public static ArchiveJpaEntity from(Archive archive) {
        return new ArchiveJpaEntity(archive);
    }

    public Archive toDomain() {
        return Archive.of(id, title, content, startDate, endDate, location, departmentName, imageIds);
    }
}
