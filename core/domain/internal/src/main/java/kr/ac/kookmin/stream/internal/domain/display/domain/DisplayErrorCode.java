package kr.ac.kookmin.stream.internal.domain.display.domain;

import kr.ac.kookmin.stream.common.ErrorCode;
import kr.ac.kookmin.stream.common.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum DisplayErrorCode implements ErrorCode {

    INVALID_DISPLAY_SCHEDULE_PERIOD(ErrorStatus.BAD_REQUEST, "종료일은 시작일보다 앞설 수 없습니다."),
    DISPLAY_POSTER_NOT_FOUND(ErrorStatus.NOT_FOUND, "존재하지 않는 포스터입니다."),
    DISPLAY_SCHEDULE_NOT_FOUND(ErrorStatus.NOT_FOUND, "존재하지 않는 일정입니다.");

    private final int status;
    private final String message;
}
