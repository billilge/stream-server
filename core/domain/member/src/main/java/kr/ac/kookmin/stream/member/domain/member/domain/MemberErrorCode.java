package kr.ac.kookmin.stream.member.domain.member.domain;

import kr.ac.kookmin.stream.common.ErrorCode;
import kr.ac.kookmin.stream.common.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    MEMBER_NOT_FOUND(ErrorStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    DEPARTMENT_NOT_ALLOWED(ErrorStatus.FORBIDDEN, "소프트웨어융합대학 학생만 이용할 수 있습니다.");

    private final int status;
    private final String message;
}
