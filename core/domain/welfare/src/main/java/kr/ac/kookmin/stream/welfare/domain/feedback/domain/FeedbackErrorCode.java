package kr.ac.kookmin.stream.welfare.domain.feedback.domain;

import kr.ac.kookmin.stream.common.ErrorCode;
import kr.ac.kookmin.stream.common.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum FeedbackErrorCode implements ErrorCode {

    INVALID_FEEDBACK_ROUND(ErrorStatus.BAD_REQUEST, "유효하지 않은 열린피드백 회차입니다."),
    FEEDBACK_NOT_FOUND(ErrorStatus.NOT_FOUND, "존재하지 않는 피드백입니다."),
    FEEDBACK_NOT_OPEN(ErrorStatus.CONFLICT, "현재 열린피드백 질문 접수 기간이 아닙니다.");

    private final int status;
    private final String message;
}
