package kr.ac.kookmin.stream.welfare.domain.notice.domain;

import kr.ac.kookmin.stream.common.ErrorCode;
import kr.ac.kookmin.stream.common.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum NoticeErrorCode implements ErrorCode {

    NOTICE_NOT_FOUND(ErrorStatus.NOT_FOUND, "공지를 찾을 수 없습니다."),
    NOTICE_INVALID_CURSOR(ErrorStatus.BAD_REQUEST, "유효하지 않은 커서입니다."),
    NOTICE_UPDATE_CONFLICT(ErrorStatus.CONFLICT, "다른 관리자가 먼저 공지를 수정했습니다. 새로고침한 뒤 다시 수정해 주세요.");

    private final int status;
    private final String message;
}
