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
    LOCKER_SECTION_NOT_FOUND(ErrorStatus.NOT_FOUND, "사물함 구역을 찾을 수 없습니다."),
    INVALID_LOCKER_PERIOD_RANGE(ErrorStatus.BAD_REQUEST, "신청 기간과 사용 기간의 순서가 올바르지 않습니다."),
    LOCKER_PERIOD_SEMESTER_DUPLICATED(ErrorStatus.CONFLICT, "이미 등록된 학기입니다."),
    LOCKER_PERIOD_OVERLAPPED(ErrorStatus.CONFLICT, "다른 회차와 기간이 겹칩니다."),
    LOCKER_PERIOD_ALREADY_ENDED(ErrorStatus.CONFLICT, "종료된 회차는 수정할 수 없습니다."),
    LOCKER_PERIOD_APPLY_ALREADY_STARTED(ErrorStatus.CONFLICT, "신청이 시작된 회차는 신청 시작일을 바꿀 수 없습니다."),
    LOCKER_PERIOD_HAS_APPLICATIONS(ErrorStatus.CONFLICT, "신청이 있는 회차는 삭제할 수 없습니다."),
    LOCKER_ALREADY_ASSIGNED(ErrorStatus.CONFLICT, "다른 사용자가 먼저 신청한 사물함입니다.");

    private final int status;
    private final String message;
}
