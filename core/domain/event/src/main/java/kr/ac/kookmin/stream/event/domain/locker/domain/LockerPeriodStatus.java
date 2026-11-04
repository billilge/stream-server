package kr.ac.kookmin.stream.event.domain.locker.domain;

/**
 * 운영 회차의 진행 상태. 저장하지 않고 서버 현재 시각으로 그때그때 계산한다.
 */
public enum LockerPeriodStatus {
    /** 신청 시작 전 */
    UPCOMING,
    /** 신청 시작 이후 사용 종료일까지 */
    IN_PROGRESS,
    /** 사용 종료일 이후 */
    ENDED
}
