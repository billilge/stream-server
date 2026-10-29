package kr.ac.kookmin.stream.api.app.event.locker.response;

import java.util.Objects;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;

public record LockerResponse(
    Long lockerId,
    int lockerNumber,
    String lockerLabel,
    boolean isAvailable,
    boolean isMine
) {

    /**
     * @param applied    해당 운영 회차에 이 사물함이 이미 신청되었는지
     * @param myLockerId 조회한 회원이 신청한 사물함. 신청하지 않았으면 {@code null}
     */
    public static LockerResponse of(Locker locker, boolean applied, Long myLockerId) {
        return new LockerResponse(
            locker.getId(),
            locker.getLockerNumber(),
            locker.getLockerLabel(),
            locker.isSelectable(applied),
            Objects.equals(locker.getId(), myLockerId)
        );
    }
}
