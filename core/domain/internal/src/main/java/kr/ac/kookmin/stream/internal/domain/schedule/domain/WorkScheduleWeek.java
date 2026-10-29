package kr.ac.kookmin.stream.internal.domain.schedule.domain;

import java.util.List;

// 한 주의 근무 시간표. editable은 그 주가 지난 주가 아니어서 근무 시간을 바꿀 수 있는지다
public record WorkScheduleWeek(WorkWeek week, boolean editable, List<WorkSchedule> schedules) {

    public static WorkScheduleWeek of(WorkWeek week, boolean editable, List<WorkSchedule> schedules) {
        return new WorkScheduleWeek(week, editable, schedules);
    }
}
