package kr.ac.kookmin.stream.event.domain.locker.domain;

/**
 * 사물함 신청 한 건과 배정된 사물함, 사용 기간을 알려줄 운영 회차의 묶음. 신청 직후 결과와 회원별 신청 내역에 함께 쓴다.
 * <p>
 * 필드를 복사하지 않고 도메인 객체를 그대로 담는다. 응답에 필요한 값은 표현 계층이 꺼내 쓴다.
 */
public record LockerApplicationResult(LockerApplication application, Locker locker, LockerPeriod period) {
}
