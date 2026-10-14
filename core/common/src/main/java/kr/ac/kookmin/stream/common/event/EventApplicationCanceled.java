package kr.ac.kookmin.stream.common.event;

/** 회원이 행사 신청을 취소했다. 행사명은 알림 문구에 쓰려고 취소 시점의 값을 싣는다. */
public record EventApplicationCanceled(Long memberId, Long eventId, String eventTitle) implements DomainEvent {}
