package kr.ac.kookmin.stream.common.event;

import java.util.List;

/** 모집 중인 행사의 신청이 내일 마감된다. 알림은 신청자를 뺀 회원에게 간다. */
public record EventApplyClosingTomorrow(Long eventId, String eventTitle, List<Long> applicantIds) implements DomainEvent {}
