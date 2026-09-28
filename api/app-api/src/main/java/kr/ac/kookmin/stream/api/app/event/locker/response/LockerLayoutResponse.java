package kr.ac.kookmin.stream.api.app.event.locker.response;

import java.util.List;
import java.util.Set;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;

public record LockerLayoutResponse(List<LockerResponse> lockers) {

    /**
     * @param appliedLockerIds 해당 운영 회차에 이미 신청된 사물함 식별자
     * @param myLockerId       조회한 회원이 신청한 사물함. 신청하지 않았으면 {@code null}
     */
    public static LockerLayoutResponse of(
        List<Locker> lockers,
        Set<Long> appliedLockerIds,
        Long myLockerId
    ) {
        return new LockerLayoutResponse(lockers.stream()
            .map(locker -> LockerResponse.of(locker, appliedLockerIds.contains(locker.getId()), myLockerId))
            .toList());
    }
}
