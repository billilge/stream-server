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
    private String title;
    private DisplaySchedulePeriod period;

    public static DisplayCalendarSchedule create(DisplayCalendarScheduleCreateCommand command) {
        return new DisplayCalendarSchedule(null, command.title(), command.period());
    }

    public static DisplayCalendarSchedule of(Long id, String title, LocalDate startDate, LocalDate endDate) {
        return new DisplayCalendarSchedule(id, title, new DisplaySchedulePeriod(startDate, endDate));
    }

    public void update(DisplayCalendarScheduleUpdateCommand command) {
        this.title = command.title();
        this.period = command.period();
    }

    public boolean includes(LocalDate date) {
        return period.includes(date);
    }
}
