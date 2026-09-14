package kr.ac.kookmin.stream.event.domain.event.domain;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EventApplication {

    private Long id;
    private Long eventId;
    private Long memberId;
    private EventApplicationStatus status;
    private LocalDateTime appliedAt;
    private LocalDateTime canceledAt;

    public static EventApplication of(
        Long id,
        Long eventId,
        Long memberId,
        EventApplicationStatus status,
        LocalDateTime appliedAt,
        LocalDateTime canceledAt
    ) {
        return new EventApplication(id, eventId, memberId, status, appliedAt, canceledAt);
    }

    /**
     * 새 신청을 만든다. 식별자는 저장 시 부여되고, 신청 직후 상태는 항상 APPLIED다.
     */
    public static EventApplication create(Long eventId, Long memberId, LocalDateTime appliedAt) {
        return new EventApplication(null, eventId, memberId, EventApplicationStatus.APPLIED, appliedAt, null);
    }
}
