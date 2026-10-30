package kr.ac.kookmin.stream.common.event;

/** 운영진이 회원에게 사물함을 직접 배정했다. 회원이 신청한 적 없는 배정이라 {@link LockerAssigned}와 문구가 다르다. */
public record LockerAssignedByAdmin(Long memberId, Long lockerPeriodId) implements DomainEvent {}
