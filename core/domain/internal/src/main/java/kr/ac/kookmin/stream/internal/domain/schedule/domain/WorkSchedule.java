package kr.ac.kookmin.stream.internal.domain.schedule.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import kr.ac.kookmin.stream.common.BusinessException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 빌릴게 근무 시간 1건. 근무자 한 명이 근무일 하루의 startHour시부터 endHour시까지 근무한다.
 * 근무는 평일 10~17시에 1시간 단위로 서고, 점심시간(12~13시)에는 서지 않는다.
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkSchedule {

    private static final int OPEN_HOUR = 10;
    private static final int CLOSE_HOUR = 17;
    private static final int LUNCH_START_HOUR = 12;
    private static final int LUNCH_END_HOUR = 13;

    private Long id;
    private Long memberId;
    private LocalDate workDate;
    private int startHour;
    private int endHour;

    public static WorkSchedule create(Long memberId, WorkScheduleCommand command) {
        validateWorkDate(command.workDate());
        validateWorkHours(command.startHour(), command.endHour());
        return new WorkSchedule(null, memberId, command.workDate(), command.startHour(), command.endHour());
    }

    public static WorkSchedule of(
        Long id,
        Long memberId,
        LocalDate workDate,
        int startHour,
        int endHour
    ) {
        return new WorkSchedule(id, memberId, workDate, startHour, endHour);
    }

    // 근무자·근무일·시간을 한꺼번에 바꾼다. 등록과 같은 규칙으로 검증한다
    public void change(Long memberId, WorkScheduleCommand command) {
        validateWorkDate(command.workDate());
        validateWorkHours(command.startHour(), command.endHour());
        this.memberId = memberId;
        this.workDate = command.workDate();
        this.startHour = command.startHour();
        this.endHour = command.endHour();
    }

    public DayOfWeek getDayOfWeek() {
        return workDate.getDayOfWeek();
    }

    // 끝 시각은 포함하지 않으므로 10~11시와 11~12시처럼 맞닿기만 한 시간대는 겹치지 않는다
    public boolean overlaps(WorkSchedule other) {
        return workDate.equals(other.workDate)
            && startHour < other.endHour
            && other.startHour < endHour;
    }

    private static void validateWorkDate(LocalDate workDate) {
        DayOfWeek dayOfWeek = workDate.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            throw new BusinessException(WorkScheduleErrorCode.INVALID_WORK_DATE);
        }
    }

    // 점심시간에 걸치는 시간대(예: 11~14시)는 받지 않는다. 오전·오후 두 건으로 나눠 등록한다
    private static void validateWorkHours(int startHour, int endHour) {
        boolean outOfOpenHours = startHour < OPEN_HOUR || endHour > CLOSE_HOUR;
        boolean overlapsLunch = startHour < LUNCH_END_HOUR && endHour > LUNCH_START_HOUR;
        if (outOfOpenHours || startHour >= endHour || overlapsLunch) {
            throw new BusinessException(WorkScheduleErrorCode.INVALID_WORK_HOURS);
        }
    }
}
