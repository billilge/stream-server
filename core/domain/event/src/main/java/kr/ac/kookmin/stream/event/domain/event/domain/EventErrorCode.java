package kr.ac.kookmin.stream.event.domain.event.domain;

import kr.ac.kookmin.stream.common.ErrorCode;
import kr.ac.kookmin.stream.common.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum EventErrorCode implements ErrorCode {

    EVENT_NOT_FOUND(ErrorStatus.NOT_FOUND, "행사를 찾을 수 없습니다."),
    ALREADY_CLOSED(ErrorStatus.CONFLICT, "행사 마감되었습니다.");

    private final int status;
    private final String message;
}
