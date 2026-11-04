package kr.ac.kookmin.stream.common.event;

/** 운영진이 회원의 행사 신청을 취소했다. 행사명은 알림 문구에 쓰려고 취소 시점의 값을 싣는다. */
public record EventApplicationCanceledByAdmin(Long memberId, Long eventId, Long applicationId, String eventTitle) implements DomainEvent {}
