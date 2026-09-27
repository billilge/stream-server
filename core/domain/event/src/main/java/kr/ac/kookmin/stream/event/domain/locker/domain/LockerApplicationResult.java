package kr.ac.kookmin.stream.event.domain.locker.domain;

/**
 * 사물함 신청 결과. 생성된 신청과 배정된 사물함, 사용 기간을 알려줄 운영 회차를 함께 돌려준다.
 */
public record LockerApplicationResult(LockerApplication application, Locker locker, LockerPeriod period) {
}
