package kr.ac.kookmin.stream.common.event;

/** 회원이 사물함을 신청해 배정받았다. 신청과 배정이 동시에 일어난다. */
public record LockerAssigned(Long memberId, Long lockerPeriodId) implements DomainEvent {}
