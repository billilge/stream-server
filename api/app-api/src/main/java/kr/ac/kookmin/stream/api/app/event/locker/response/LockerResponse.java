package kr.ac.kookmin.stream.api.app.event.locker.response;

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

    public static LockerResponse from(LockerAvailability locker) {
        return new LockerResponse(
            locker.lockerId(),
            locker.lockerLabel(),
            locker.lockerNumber(),
            locker.rowNo(),
            locker.columnNo(),
            locker.available(),
            locker.mine()
        );
    }
}
