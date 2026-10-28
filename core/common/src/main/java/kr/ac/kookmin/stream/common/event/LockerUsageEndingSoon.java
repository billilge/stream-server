package kr.ac.kookmin.stream.common.event;

import java.util.List;

/** 사물함 운영 회차의 이용 기간이 곧 끝난다. 알림은 그 회차 신청자에게 간다. */
public record LockerUsageEndingSoon(Long lockerPeriodId, List<Long> applicantIds) implements DomainEvent {}
