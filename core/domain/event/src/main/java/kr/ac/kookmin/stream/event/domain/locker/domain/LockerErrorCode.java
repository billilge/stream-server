package kr.ac.kookmin.stream.event.domain.locker.domain;

import kr.ac.kookmin.stream.common.ErrorCode;
import kr.ac.kookmin.stream.common.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum LockerErrorCode implements ErrorCode {

    LOCKER_PERIOD_NOT_FOUND(ErrorStatus.NOT_FOUND, "사물함 운영 회차를 찾을 수 없습니다."),
    LOCKER_SECTION_NOT_FOUND(ErrorStatus.NOT_FOUND, "사물함 구역을 찾을 수 없습니다.");

    private final int status;
    private final String message;
}
