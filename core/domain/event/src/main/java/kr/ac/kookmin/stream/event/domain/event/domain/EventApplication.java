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

    /**
     * 신청을 취소한다. 신청 기록과 제출한 답변은 지우지 않고 상태만 CANCELED로 바꾼다.
     * 신청자 수 집계가 APPLIED만 세므로 이 상태 전환만으로 잔여 정원에 반영된다.
     *
     * @param canceledAt 취소 시각. 클라이언트가 보내지 않고 서버 시각으로 기록한다
     */
    public void cancel(LocalDateTime canceledAt) {
        this.status = EventApplicationStatus.CANCELED;
        this.canceledAt = canceledAt;
    }

    public boolean isCanceled() {
        return status == EventApplicationStatus.CANCELED;
    }
}
