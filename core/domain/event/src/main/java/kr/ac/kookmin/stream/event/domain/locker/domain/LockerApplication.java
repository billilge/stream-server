package kr.ac.kookmin.stream.event.domain.locker.domain;

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

    public static LockerApplication of(Long id, Long lockerPeriodId, Long memberId, Long lockerId) {
        return new LockerApplication(id, lockerPeriodId, memberId, lockerId);
    }
}
