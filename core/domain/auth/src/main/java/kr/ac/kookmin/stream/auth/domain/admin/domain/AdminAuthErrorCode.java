package kr.ac.kookmin.stream.auth.domain.admin.domain;

import kr.ac.kookmin.stream.common.ErrorCode;
import kr.ac.kookmin.stream.common.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum AdminAuthErrorCode implements ErrorCode {

    // 회원 없음·운영진 아님·비밀번호 불일치를 구분하지 않는다. 학번으로 운영진 여부를 알아내지 못하게 하기 위함
    INVALID_ADMIN_CREDENTIALS(ErrorStatus.UNAUTHORIZED, "학번 또는 비밀번호가 올바르지 않습니다."),
    // 로그인한 상태에서 내는 에러라 401이 아니라 400이다. 401이면 콘솔이 토큰 만료로 보고 로그아웃시킬 수 있다
    ADMIN_PASSWORD_MISMATCH(ErrorStatus.BAD_REQUEST, "현재 비밀번호가 올바르지 않습니다.");

    private final int status;
    private final String message;
}
