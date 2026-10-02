package kr.ac.kookmin.stream.event.domain.locker.domain;

/**
 * 사물함 신청 요청. 신청할 운영 회차와 고른 사물함을 담는다.
 */
public record LockerApplyCommand(Long lockerPeriodId, Long lockerId) {
}
