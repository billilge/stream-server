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
    ALREADY_CLOSED(ErrorStatus.CONFLICT, "행사 마감되었습니다."),
    CAPACITY_FULL(ErrorStatus.CONFLICT, "모집 정원이 마감되었습니다."),
    ALREADY_APPLIED(ErrorStatus.CONFLICT, "이미 신청한 행사입니다."),
    INVALID_ANSWER(ErrorStatus.BAD_REQUEST, "신청서 답변 형식이 올바르지 않습니다."),
    EVENT_INVALID_CURSOR(ErrorStatus.BAD_REQUEST, "유효하지 않은 커서입니다."),
    EVENT_INVALID_RECRUIT_STATUS(ErrorStatus.BAD_REQUEST, "유효하지 않은 모집 상태입니다.");

    private final int status;
    private final String message;
}
