package kr.ac.kookmin.stream.api.app.internal.home.response;

import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationStatus;

/**
 * 홈에 보여줄 사물함 배정 상태.
 * <p>
 * {@link #NOT_APPLIED}·{@link #APPLICABLE}은 최근 회차에 신청이 없을 때라 {@link LockerApplicationStatus}에 대응하는 값이
 * 없다. 호출부가 직접 만들고 {@link #from}은 신청이 있을 때만 쓴다.
 */
public enum HomeLockerStatus {

    NOT_APPLIED,
    APPLICABLE,
    ASSIGNED,
    EXPIRED;

    // switch 식이라 LockerApplicationStatus에 값이 늘면 컴파일이 막는다
    public static HomeLockerStatus from(LockerApplicationStatus status) {
        return switch (status) {
            case ASSIGNED -> ASSIGNED;
            case EXPIRED -> EXPIRED;
        };
    }
}
