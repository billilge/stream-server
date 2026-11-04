package kr.ac.kookmin.stream.event.domain.locker.domain;

import java.time.LocalDate;

/**
 * 연도와 학기는 바꿀 수 없어 담지 않는다. 날짜만 받는 이유는 {@link LockerPeriodCreateCommand}와 같다.
 */
public record LockerPeriodUpdateCommand(
    LocalDate applyStartDate,
    LocalDate applyEndDate,
    LocalDate usageStartDate,
    LocalDate usageEndDate
) {
}
