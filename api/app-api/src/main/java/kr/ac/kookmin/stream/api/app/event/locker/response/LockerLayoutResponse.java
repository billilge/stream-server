package kr.ac.kookmin.stream.api.app.event.locker.response;

import java.util.List;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerAvailability;

public record LockerLayoutResponse(List<LockerResponse> lockers) {

    /**
     * @param myLockerId 조회한 회원이 신청한 사물함. 신청하지 않았으면 {@code null}
     */
    public static LockerLayoutResponse of(List<LockerAvailability> lockers, Long myLockerId) {
        return new LockerLayoutResponse(lockers.stream()
            .map(locker -> LockerResponse.of(locker, myLockerId))
            .toList());
    }
}
