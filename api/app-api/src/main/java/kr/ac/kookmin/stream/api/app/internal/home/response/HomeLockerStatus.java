package kr.ac.kookmin.stream.api.app.internal.home.response;

import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationStatus;

/**
 * 홈에 보여줄 사물함 배정 상태.
 * <p>
 * 신청이 없으면 배정 자체가 없어 {@link LockerApplicationStatus}에 대응하는 값이 없다.
 * 화면은 그 경우도 하나의 상태로 그리므로 {@link #NOT_APPLIED}를 여기서 만든다 — 신청이 없는 경우는
 * 호출부가 먼저 거르므로 {@link #from}은 받지 않는다.
 */
public enum HomeLockerStatus {

    NOT_APPLIED,
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
