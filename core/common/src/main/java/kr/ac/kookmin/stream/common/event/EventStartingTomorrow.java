package kr.ac.kookmin.stream.common.event;

import java.util.List;

/** 행사가 내일 시작한다. 알림은 신청자에게 간다. */
public record EventStartingTomorrow(Long eventId, String eventTitle, List<Long> applicantIds) implements DomainEvent {}
