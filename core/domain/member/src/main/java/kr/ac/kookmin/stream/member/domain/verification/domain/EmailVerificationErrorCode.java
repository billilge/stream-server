package kr.ac.kookmin.stream.member.domain.verification.domain;

import kr.ac.kookmin.stream.common.ErrorCode;
import kr.ac.kookmin.stream.common.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum EmailVerificationErrorCode implements ErrorCode {

    EMAIL_SEND_FAILED(ErrorStatus.BAD_GATEWAY, "인증 메일을 보내지 못했습니다. 잠시 후 다시 시도해 주세요.");

    private final int status;
    private final String message;
}
