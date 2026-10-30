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
    APPLICATION_NOT_FOUND(ErrorStatus.NOT_FOUND, "행사 신청을 찾을 수 없습니다."),
    ALREADY_CANCELED(ErrorStatus.CONFLICT, "이미 취소된 신청입니다."),
    CANCEL_DEADLINE_PASSED(ErrorStatus.CONFLICT, "신청이 마감되어 취소할 수 없습니다."),
    INVALID_ANSWER(ErrorStatus.BAD_REQUEST, "신청서 답변 형식이 올바르지 않습니다."),
    EVENT_INVALID_CURSOR(ErrorStatus.BAD_REQUEST, "유효하지 않은 커서입니다."),
    EVENT_INVALID_RECRUIT_STATUS(ErrorStatus.BAD_REQUEST, "유효하지 않은 모집 상태입니다."),
    EVENT_INVALID_APPLICATION_STATUS(ErrorStatus.BAD_REQUEST, "유효하지 않은 신청 상태입니다."),
    EVENT_INVALID_PERIOD(ErrorStatus.BAD_REQUEST, "종료 일시는 시작 일시보다 늦어야 합니다."),
    EVENT_INVALID_CAPACITY(ErrorStatus.BAD_REQUEST, "선착순 모집은 1명 이상의 정원을 입력해야 합니다."),
    EVENT_INVALID_QUESTION(ErrorStatus.BAD_REQUEST, "선택형 질문에는 선택지를 1개 이상 입력해야 합니다."),
    EVENT_QUESTIONS_LOCKED(ErrorStatus.CONFLICT, "신청 내역이 있는 행사는 신청서 질문을 수정할 수 없습니다."),
    EVENT_CAPACITY_BELOW_APPLICANTS(ErrorStatus.CONFLICT, "정원을 현재 신청자 수보다 적게 줄일 수 없습니다."),
    EVENT_UNSUPPORTED_OPTION(ErrorStatus.CONFLICT, "학생회비 납부자 전용이나 기타 선택지를 쓰는 행사는 아직 공개할 수 없습니다."),
    EVENT_UPDATE_CONFLICT(ErrorStatus.CONFLICT, "다른 관리자가 먼저 행사를 수정했습니다. 새로고침한 뒤 다시 수정해 주세요.");

    private final int status;
    private final String message;
}
