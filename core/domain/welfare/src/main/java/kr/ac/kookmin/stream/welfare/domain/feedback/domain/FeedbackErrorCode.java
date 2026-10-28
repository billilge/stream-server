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
    INVALID_FEEDBACK_ROUND_STATUS(ErrorStatus.BAD_REQUEST, "유효하지 않은 회차 상태입니다."),
    INVALID_FEEDBACK_ROUND_PERIOD(ErrorStatus.BAD_REQUEST, "질문 접수 마감일은 시작일 이후여야 합니다."),
    FEEDBACK_NOT_IN_ROUND(ErrorStatus.BAD_REQUEST, "해당 회차에 접수된 질문이 아닙니다."),
    FEEDBACK_ANSWER_NOT_FOUND(ErrorStatus.BAD_REQUEST, "공개할 답변이 없습니다."),
    FEEDBACK_NOT_FOUND(ErrorStatus.NOT_FOUND, "존재하지 않는 피드백입니다."),
    FEEDBACK_ROUND_NOT_FOUND(ErrorStatus.NOT_FOUND, "존재하지 않는 열린피드백 회차입니다."),
    FEEDBACK_NOT_OPEN(ErrorStatus.CONFLICT, "현재 열린피드백 질문 접수 기간이 아닙니다."),
    FEEDBACK_ROUND_OVERLAPPED(ErrorStatus.CONFLICT, "이미 등록된 회차와 질문 접수 기간이 겹칩니다."),
    FEEDBACK_QUESTION_PERIOD_NOT_ENDED(ErrorStatus.CONFLICT, "질문 접수가 끝난 회차만 답변을 공개할 수 있습니다.");

    private final int status;
    private final String message;
}
