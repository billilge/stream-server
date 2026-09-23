package kr.ac.kookmin.stream.api.app.event.locker.response;

import java.util.List;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerAvailability;

public record LockerLayoutResponse(List<LockerResponse> lockers) {

    public static LockerLayoutResponse from(List<LockerAvailability> lockers) {
        return new LockerLayoutResponse(lockers.stream().map(LockerResponse::from).toList());
    }
}
