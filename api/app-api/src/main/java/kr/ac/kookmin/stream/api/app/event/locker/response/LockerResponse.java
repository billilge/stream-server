package kr.ac.kookmin.stream.api.app.event.locker.response;

import java.util.Objects;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerAvailability;

public record LockerResponse(
    Long lockerId,
    String lockerLabel,
    int lockerNumber,
    int rowNo,
    int columnNo,
    boolean isAvailable,
    boolean isMine
) {

    /**
     * @param myLockerId 조회한 회원이 신청한 사물함. 신청하지 않았으면 {@code null}
     */
    public static LockerResponse of(LockerAvailability locker, Long myLockerId) {
        return new LockerResponse(
            locker.lockerId(),
            locker.lockerLabel(),
            locker.lockerNumber(),
            locker.rowNo(),
            locker.columnNo(),
            locker.available(),
            Objects.equals(locker.lockerId(), myLockerId)
        );
    }
}
