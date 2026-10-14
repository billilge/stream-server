package kr.ac.kookmin.stream.common.event;

/** 회원이 행사를 신청했다. 행사명은 알림 문구에 쓰려고 신청 시점의 값을 싣는다. */
public record EventApplied(Long memberId, Long eventId, String eventTitle) implements DomainEvent {}
