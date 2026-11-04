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
import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.db.common.BaseSoftDeleteEntity;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

// 바뀐 컬럼만 UPDATE한다(@DynamicUpdate). 도메인 행사에는 삭제 여부가 없어 저장할 때마다 is_deleted가 false로 들어가므로,
// 공개 전환·수정과 삭제가 겹쳤을 때 삭제 표시를 되써서 지운 행사가 되살아나는 일을 막는다.
@Entity
@Table(
    name = "events",
    indexes = @Index(
        name = "idx_events_is_published_is_deleted_event_start_at",
        columnList = "is_published, is_deleted, event_start_at"
    )
)
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventJpaEntity extends BaseSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    private String place;

    @Column(name = "event_start_at", nullable = false)
    private LocalDateTime eventStartAt;

    @Column(name = "event_end_at")
    private LocalDateTime eventEndAt;

    @Column(name = "apply_start_at", nullable = false)
    private LocalDateTime applyStartAt;

    @Column(name = "apply_end_at", nullable = false)
    private LocalDateTime applyEndAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "recruit_type", nullable = false, length = 30)
    private RecruitType recruitType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "image_ids", columnDefinition = "json")
    private List<Long> imageIds;

    @Column(nullable = false)
    private int capacity;

    @Column(name = "fee_paid_only", nullable = false)
    private boolean feePaidOnly;

    @Column(name = "is_published", nullable = false)
    private boolean isPublished;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    private EventJpaEntity(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.place = event.getPlace();
        this.eventStartAt = event.getEventStartAt();
        this.eventEndAt = event.getEventEndAt();
        this.applyStartAt = event.getApplyStartAt();
        this.applyEndAt = event.getApplyEndAt();
        this.recruitType = event.getRecruitType();
        this.imageIds = event.getImageIds();
        this.capacity = event.getCapacity();
        this.feePaidOnly = event.isFeePaidOnly();
        this.isPublished = event.isPublished();
        this.createdBy = event.getCreatedBy();
    }

    public static EventJpaEntity from(Event event) {
        return new EventJpaEntity(event);
    }

    public Event toDomain() {
        return Event.of(id, title, description, place, eventStartAt, eventEndAt,
            applyStartAt, applyEndAt, recruitType, imageIds, capacity, feePaidOnly,
            isPublished, createdBy, getUpdatedAt());
    }
}
