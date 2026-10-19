package kr.ac.kookmin.stream.outbox;

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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

// created_at은 DB 기본값으로만 채우고 읽지 않아서 매핑하지 않는다
@Entity
@Table(
    name = "outbox_events",
    indexes = {
        @Index(name = "idx_outbox_events_status_outbox_event_id", columnList = "status, outbox_event_id")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEventJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outbox_event_id")
    private Long id;

    @Column(name = "event_type", nullable = false, length = 200)
    private String eventType;

    /** 이벤트를 직렬화한 JSON 원문을 문자열 그대로 읽고 쓴다. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "json")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxStatus status;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    private OutboxEventJpaEntity(String eventType, String payload) {
        this.eventType = eventType;
        this.payload = payload;
        this.status = OutboxStatus.PENDING;
    }

    public static OutboxEventJpaEntity pending(String eventType, String payload) {
        return new OutboxEventJpaEntity(eventType, payload);
    }

    public void markPublished(LocalDateTime publishedAt) {
        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = publishedAt;
    }

    // 실패 횟수가 한도에 닿으면 FAILED로 바꾸고 true를 돌려준다
    public boolean recordFailure(int maxAttempts) {
        this.attempts++;
        if (attempts < maxAttempts) {
            return false;
        }
        this.status = OutboxStatus.FAILED;
        return true;
    }
}
