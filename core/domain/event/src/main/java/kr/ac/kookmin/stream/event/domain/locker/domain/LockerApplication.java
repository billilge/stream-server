package kr.ac.kookmin.stream.event.domain.locker.domain;

import java.time.LocalDateTime;
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

    public static LockerApplication of(
        Long id,
        Long lockerPeriodId,
        Long memberId,
        Long lockerId,
        LocalDateTime appliedAt
    ) {
        return new LockerApplication(id, lockerPeriodId, memberId, lockerId, appliedAt);
    }

    /**
     * 새 신청을 만든다. 식별자는 저장 시 부여된다. 승인 절차 없이 신청이 곧 배정이다.
     *
     * @param appliedAt 신청 시각. 클라이언트가 보내지 않고 서버 시각으로 기록한다
     */
    public static LockerApplication create(Long lockerPeriodId, Long memberId, Long lockerId, LocalDateTime appliedAt) {
        return new LockerApplication(null, lockerPeriodId, memberId, lockerId, appliedAt);
    }
}
