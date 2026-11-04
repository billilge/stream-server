package kr.ac.kookmin.stream.event.domain.locker.domain;

import java.time.LocalDate;

/**
 * 날짜만 받는다. 신청 기간의 시·분·초는 도메인이 정한다(시작일 00:00:00 ~ 마감일 23:59:59).
 */
public record LockerPeriodCreateCommand(
    int year,
    int semester,
    LocalDate applyStartDate,
    LocalDate applyEndDate,
    LocalDate usageStartDate,
    LocalDate usageEndDate
) {
}
