package kr.ac.kookmin.stream.api.admin.internal.display.response;

import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayCalendarSchedule;

public record DisplayCalendarScheduleCreateResponse(Long scheduleId) {

    public static DisplayCalendarScheduleCreateResponse from(DisplayCalendarSchedule schedule) {
        return new DisplayCalendarScheduleCreateResponse(schedule.getId());
    }
}
