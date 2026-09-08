package kr.ac.kookmin.stream.db.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.db.common.BaseSoftDeleteEntity;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "events")
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

    @Column(nullable = false)
    private String target;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "recruit_status", nullable = false, length = 30)
    private RecruitStatus recruitStatus;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    private EventJpaEntity(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.target = event.getTarget();
        this.place = event.getPlace();
        this.eventStartAt = event.getEventStartAt();
        this.eventEndAt = event.getEventEndAt();
        this.applyStartAt = event.getApplyStartAt();
        this.applyEndAt = event.getApplyEndAt();
        this.recruitType = event.getRecruitType();
        this.imageIds = event.getImageIds();
        this.capacity = event.getCapacity();
        this.recruitStatus = event.getRecruitStatus();
        this.createdBy = event.getCreatedBy();
    }

    public static EventJpaEntity from(Event event) {
        return new EventJpaEntity(event);
    }

    public Event toDomain() {
        return Event.of(id, title, description, target, place, eventStartAt, eventEndAt,
            applyStartAt, applyEndAt, recruitType, imageIds, capacity, recruitStatus, createdBy);
    }
}
