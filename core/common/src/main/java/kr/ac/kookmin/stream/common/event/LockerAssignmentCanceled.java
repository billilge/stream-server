package kr.ac.kookmin.stream.common.event;

/** 운영진이 회원의 사물함 배정을 취소했다. 회원 화면에서는 신청이 사라지므로 알림이 유일한 전달 수단이다. */
public record LockerAssignmentCanceled(Long memberId, Long lockerPeriodId) implements DomainEvent {}
