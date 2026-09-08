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
import kr.ac.kookmin.stream.db.common.BaseTimeEntity;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "event_applications",
    indexes = {
        @Index(name = "idx_event_applications_event_id_member_id", columnList = "event_id, member_id"),
        @Index(name = "idx_event_applications_member_id", columnList = "member_id")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventApplicationJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_application_id")
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventApplicationStatus status;

    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    private EventApplicationJpaEntity(EventApplication application) {
        this.id = application.getId();
        this.eventId = application.getEventId();
        this.memberId = application.getMemberId();
        this.status = application.getStatus();
        this.appliedAt = application.getAppliedAt();
        this.canceledAt = application.getCanceledAt();
    }

    public static EventApplicationJpaEntity from(EventApplication application) {
        return new EventApplicationJpaEntity(application);
    }

    public EventApplication toDomain() {
        return EventApplication.of(id, eventId, memberId, status, appliedAt, canceledAt);
    }
}
