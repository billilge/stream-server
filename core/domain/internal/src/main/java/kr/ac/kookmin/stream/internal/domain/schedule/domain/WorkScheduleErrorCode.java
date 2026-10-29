package kr.ac.kookmin.stream.internal.domain.schedule.domain;

import kr.ac.kookmin.stream.common.ErrorCode;
import kr.ac.kookmin.stream.common.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum WorkScheduleErrorCode implements ErrorCode {

    INVALID_WORK_DATE(ErrorStatus.BAD_REQUEST, "유효하지 않은 근무일입니다."),
    INVALID_WORK_HOURS(ErrorStatus.BAD_REQUEST, "유효하지 않은 근무 시간입니다."),
    PAST_WEEK_NOT_EDITABLE(ErrorStatus.BAD_REQUEST, "지난 주의 근무 시간은 변경할 수 없습니다."),
    WORK_SCHEDULE_NOT_FOUND(ErrorStatus.NOT_FOUND, "존재하지 않는 근무 시간입니다."),
    WORKER_NOT_FOUND(ErrorStatus.NOT_FOUND, "근무자로 배정할 수 없는 회원입니다."),
    WORK_SCHEDULE_CONFLICT(ErrorStatus.CONFLICT, "이미 근무자가 배정된 시간입니다.");

    private final int status;
    private final String message;
}
