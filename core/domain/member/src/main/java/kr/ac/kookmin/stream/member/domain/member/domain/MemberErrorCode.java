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
    DEPARTMENT_NOT_ALLOWED(ErrorStatus.FORBIDDEN, "소프트웨어융합대학 학생만 이용할 수 있습니다."),
    REQUIRED_TERMS_NOT_AGREED(ErrorStatus.BAD_REQUEST, "필수 약관에 모두 동의해 주세요."),
    ALREADY_SIGNED_UP(ErrorStatus.CONFLICT, "이미 회원가입을 마쳤습니다."),
    PHONE_NUMBER_ALREADY_EXISTS(ErrorStatus.CONFLICT, "이미 사용 중인 전화번호입니다."),
    EMAIL_ALREADY_EXISTS(ErrorStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    COUNCIL_DEPARTMENT_REQUIRED(ErrorStatus.BAD_REQUEST, "학생회 부서를 선택해 주세요."),
    ALREADY_COUNCIL_REGISTERED(ErrorStatus.CONFLICT, "이미 관리자로 등록된 회원입니다."),
    NOT_COUNCIL_REGISTERED(ErrorStatus.BAD_REQUEST, "관리자로 등록되지 않은 회원입니다."),
    LAST_PRESIDENCY_ADMIN_REQUIRED(ErrorStatus.CONFLICT, "회장단 관리자가 최소 한 명은 있어야 합니다.");

    private final int status;
    private final String message;
}
