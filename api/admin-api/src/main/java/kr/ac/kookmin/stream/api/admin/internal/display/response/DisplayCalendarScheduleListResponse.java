package kr.ac.kookmin.stream.api.admin.internal.display.response;

import java.time.LocalDate;
import java.util.List;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayCalendarSchedule;

public record DisplayCalendarScheduleListResponse(List<Schedule> schedules) {

    public static DisplayCalendarScheduleListResponse from(List<DisplayCalendarSchedule> schedules) {
        return new DisplayCalendarScheduleListResponse(schedules.stream()
            .map(Schedule::from)
            .toList());
    }

    // 하루 일정은 startDate와 endDate가 같다
    public record Schedule(Long scheduleId, String title, LocalDate startDate, LocalDate endDate) {

        public static Schedule from(DisplayCalendarSchedule schedule) {
            return new Schedule(schedule.getId(), schedule.getTitle(), schedule.getStartDate(), schedule.getEndDate());
        }
    }
}
