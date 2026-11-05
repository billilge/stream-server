package kr.ac.kookmin.stream.internal.domain.display.domain;

import java.time.LocalDate;
import kr.ac.kookmin.stream.common.BusinessException;
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
    private LocalDate startDate;
    private LocalDate endDate;

    public static DisplayCalendarSchedule create(DisplayCalendarScheduleCreateCommand command) {
        validatePeriod(command.startDate(), command.endDate());
        return new DisplayCalendarSchedule(null, command.title(), command.startDate(), command.endDate());
    }

    public static DisplayCalendarSchedule of(Long id, String title, LocalDate startDate, LocalDate endDate) {
        return new DisplayCalendarSchedule(id, title, startDate, endDate);
    }

    public void update(DisplayCalendarScheduleUpdateCommand command) {
        validatePeriod(command.startDate(), command.endDate());
        this.title = command.title();
        this.startDate = command.startDate();
        this.endDate = command.endDate();
    }

    // 시작일·종료일 당일도 기간에 든다
    public boolean includes(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    // 시작일과 종료일이 같은 하루 일정은 허용한다
    private static void validatePeriod(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new BusinessException(DisplayErrorCode.INVALID_DISPLAY_SCHEDULE_PERIOD);
        }
    }
}
