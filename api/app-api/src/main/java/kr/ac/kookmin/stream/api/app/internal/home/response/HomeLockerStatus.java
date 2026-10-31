package kr.ac.kookmin.stream.api.app.internal.home.response;

import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationStatus;

/**
 * 홈에 보여줄 사물함 배정 상태.
 * <p>
 * 신청이 없으면 배정 자체가 없어 {@link LockerApplicationStatus}에 대응하는 값이 없다.
 * 화면은 그 경우도 하나의 상태로 그리므로 {@link #NOT_APPLIED}를 여기서 만든다.
 */
public enum HomeLockerStatus {

    NOT_APPLIED,
    ASSIGNED,
    EXPIRED;

    public static HomeLockerStatus from(LockerApplicationStatus status) {
        return status == null ? NOT_APPLIED : valueOf(status.name());
    }
}
