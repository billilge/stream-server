package kr.ac.kookmin.stream.internal.domain.display.domain;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DisplayCalendarSchedule {

    private Long id;
    private LocalDate date;
    private String schedules;

    public static DisplayCalendarSchedule of(Long id, LocalDate date, String schedules) {
        return new DisplayCalendarSchedule(id, date, schedules);
    }
}
