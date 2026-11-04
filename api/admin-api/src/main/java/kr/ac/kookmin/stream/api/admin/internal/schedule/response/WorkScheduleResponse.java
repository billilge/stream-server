package kr.ac.kookmin.stream.api.admin.internal.schedule.response;

import java.time.DayOfWeek;
import java.time.LocalDate;
import kr.ac.kookmin.stream.api.admin.internal.schedule.usecase.MemberWorkSchedule;
import kr.ac.kookmin.stream.internal.domain.schedule.domain.WorkSchedule;

// 근무 시간 1건. name·studentId는 탈퇴(소프트 삭제)한 회원의 근무 시간이면 null이다
public record WorkScheduleResponse(
    Long scheduleId,
    Long memberId,
    String name,
    String studentId,
    LocalDate workDate,
    DayOfWeek dayOfWeek,
    int startHour,
    int endHour
) {

    public static WorkScheduleResponse from(MemberWorkSchedule memberWorkSchedule) {
        WorkSchedule schedule = memberWorkSchedule.schedule();

        return new WorkScheduleResponse(
            schedule.getId(),
            schedule.getMemberId(),
            memberWorkSchedule.memberName(),
            memberWorkSchedule.memberStudentId(),
            schedule.getWorkDate(),
            schedule.getDayOfWeek(),
            schedule.getStartHour(),
            schedule.getEndHour()
        );
    }
}
