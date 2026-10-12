package kr.ac.kookmin.stream.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    INVALID_INPUT(ErrorStatus.BAD_REQUEST, "유효하지 않은 입력값입니다."),
    UNAUTHORIZED(ErrorStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(ErrorStatus.FORBIDDEN, "접근 권한이 없습니다."),
    OPTIMISTIC_LOCK_CONFLICT(ErrorStatus.CONFLICT, "요청이 몰려 처리하지 못했습니다. 잠시 후 다시 시도해 주세요."),
    INTERNAL_SERVER_ERROR(ErrorStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류입니다. 다시 시도해 주세요.");

    private final int status;
    private final String message;
}
