package kr.ac.kookmin.stream.common;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, Object... formatArgs) {
        super(formatArgs.length == 0 ? errorCode.message() : errorCode.message().formatted(formatArgs));
        this.errorCode = errorCode;
    }
}
