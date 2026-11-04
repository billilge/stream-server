package kr.ac.kookmin.stream.internal.domain.schedule.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

// 근무 시간표의 한 주(월~금). 토·일은 그 앞 월요일의 주에 속한다
public record WorkWeek(LocalDate startDate, LocalDate endDate) {

    public static WorkWeek from(LocalDate date) {
        LocalDate monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return new WorkWeek(monday, monday.with(DayOfWeek.FRIDAY));
    }

    // today가 속한 주보다 앞선 주면 지난 주다. 이번 주는 이미 지난 요일이어도 지난 주가 아니다
    public boolean isPastAt(LocalDate today) {
        return startDate.isBefore(from(today).startDate());
    }
}
