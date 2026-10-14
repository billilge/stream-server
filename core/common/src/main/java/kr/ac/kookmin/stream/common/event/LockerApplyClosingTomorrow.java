package kr.ac.kookmin.stream.common.event;

import java.util.List;

/** 사물함 운영 회차의 신청이 내일 마감된다. 알림은 그 회차 신청자를 뺀 회원에게 간다. */
public record LockerApplyClosingTomorrow(Long lockerPeriodId, List<Long> applicantIds) implements DomainEvent {}
