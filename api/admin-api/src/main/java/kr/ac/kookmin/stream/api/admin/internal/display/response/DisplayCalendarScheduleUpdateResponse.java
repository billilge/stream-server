package kr.ac.kookmin.stream.api.admin.internal.display.response;

import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayCalendarSchedule;

public record DisplayCalendarScheduleUpdateResponse(Long scheduleId) {

    public static DisplayCalendarScheduleUpdateResponse from(DisplayCalendarSchedule schedule) {
        return new DisplayCalendarScheduleUpdateResponse(schedule.getId());
    }
}
