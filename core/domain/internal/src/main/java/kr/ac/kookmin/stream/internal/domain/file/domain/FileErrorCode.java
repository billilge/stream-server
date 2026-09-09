package kr.ac.kookmin.stream.internal.domain.file.domain;

import kr.ac.kookmin.stream.common.ErrorCode;
import kr.ac.kookmin.stream.common.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum FileErrorCode implements ErrorCode {

    FILE_NOT_FOUND(ErrorStatus.NOT_FOUND, "존재하지 않는 파일입니다."),
    UNSUPPORTED_FILE_EXTENSION(ErrorStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다."),
    FILE_SIZE_EXCEEDED(ErrorStatus.BAD_REQUEST, "파일의 최대 업로드 용량을 초과했습니다.");

    private final int status;
    private final String message;
}
