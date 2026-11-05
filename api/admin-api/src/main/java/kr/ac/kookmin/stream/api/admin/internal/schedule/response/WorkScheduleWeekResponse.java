package kr.ac.kookmin.stream.api.admin.internal.schedule.response;

import java.time.LocalDate;
import java.util.List;
import kr.ac.kookmin.stream.api.admin.internal.schedule.usecase.MemberWorkScheduleWeek;

// 한 주(월~금)의 근무 시간표. editable이 false면 지난 주라 등록·수정·삭제할 수 없다
public record WorkScheduleWeekResponse(
    LocalDate weekStartDate,
    LocalDate weekEndDate,
    boolean editable,
    List<WorkScheduleResponse> schedules
) {

    public static WorkScheduleWeekResponse from(MemberWorkScheduleWeek week) {
        return new WorkScheduleWeekResponse(
            week.week().startDate(),
            week.week().endDate(),
            week.editable(),
            week.schedules().stream().map(WorkScheduleResponse::from).toList()
        );
    }
}
