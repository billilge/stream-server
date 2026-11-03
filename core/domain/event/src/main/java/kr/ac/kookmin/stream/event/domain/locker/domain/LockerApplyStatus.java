package kr.ac.kookmin.stream.event.domain.locker.domain;

/**
 * 운영 회차가 지금 신청을 받는지. 사용 종료일로 가르는 {@link LockerPeriodStatus}와 달리 신청 기간으로 가른다.
 * 판정은 {@link LockerPeriod#calculateApplyStatus}가 한다.
 */
public enum LockerApplyStatus {

    BEFORE_OPEN,
    OPEN,
    CLOSED
}
