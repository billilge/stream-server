package kr.ac.kookmin.stream.event.domain.locker.domain;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.common.BusinessException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LockerApplication {

    private Long id;
    private Long lockerPeriodId;
    private Long memberId;
    private Long lockerId;
    private LocalDateTime appliedAt;
    /** 운영진이 취소했는지. 취소해도 행은 남기므로 살아 있는 신청인지는 이 값으로 가른다. */
    private boolean canceled;

    public static LockerApplication of(
        Long id,
        Long lockerPeriodId,
        Long memberId,
        Long lockerId,
        LocalDateTime appliedAt,
        boolean canceled
    ) {
        return new LockerApplication(id, lockerPeriodId, memberId, lockerId, appliedAt, canceled);
    }

    /**
     * 새 신청을 만든다. 식별자는 저장 시 부여된다. 승인 절차 없이 신청이 곧 배정이다.
     *
     * @param appliedAt 신청 시각. 클라이언트가 보내지 않고 서버 시각으로 기록한다
     */
    public static LockerApplication create(Long lockerPeriodId, Long memberId, Long lockerId, LocalDateTime appliedAt) {
        return new LockerApplication(null, lockerPeriodId, memberId, lockerId, appliedAt, false);
    }

    /**
     * 운영진이 신청을 취소한다. 행은 남기고 표시만 바꾼다 — 운영진이 그 칸이 왜 비었는지 알아야 하고,
     * 같은 학생·같은 칸에 다시 배정할 수 있어야 한다.
     */
    public void cancel() {
        if (canceled) {
            throw new BusinessException(LockerErrorCode.LOCKER_APPLICATION_ALREADY_CANCELED);
        }
        this.canceled = true;
    }
}
