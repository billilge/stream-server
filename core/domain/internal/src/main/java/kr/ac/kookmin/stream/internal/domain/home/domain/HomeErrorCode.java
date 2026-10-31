package kr.ac.kookmin.stream.internal.domain.home.domain;

import kr.ac.kookmin.stream.common.ErrorCode;
import kr.ac.kookmin.stream.common.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum HomeErrorCode implements ErrorCode {

    HOME_BANNER_NOT_FOUND(ErrorStatus.NOT_FOUND, "존재하지 않는 배너입니다.");

    private final int status;
    private final String message;
}
