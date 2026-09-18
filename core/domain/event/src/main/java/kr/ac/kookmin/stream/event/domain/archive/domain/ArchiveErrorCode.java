package kr.ac.kookmin.stream.event.domain.archive.domain;

import kr.ac.kookmin.stream.common.ErrorCode;
import kr.ac.kookmin.stream.common.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum ArchiveErrorCode implements ErrorCode {

    ARCHIVE_NOT_FOUND(ErrorStatus.NOT_FOUND, "아카이빙을 찾을 수 없습니다.");

    private final int status;
    private final String message;
}
