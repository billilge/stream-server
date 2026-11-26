package kr.ac.kookmin.stream.api.app.internal.home.response;

import kr.ac.kookmin.stream.event.domain.locker.domain.CurrentLockerStatus;

public enum HomeLockerStatus {

    NOT_APPLIED,
    APPLICABLE,
    ASSIGNED,
    EXPIRED;

    // switch 식이라 CurrentLockerStatus에 값이 늘면 컴파일이 막는다
    public static HomeLockerStatus from(CurrentLockerStatus status) {
        return switch (status) {
            case NOT_APPLIED -> NOT_APPLIED;
            case APPLICABLE -> APPLICABLE;
            case ASSIGNED -> ASSIGNED;
            case EXPIRED -> EXPIRED;
        };
    }
}
