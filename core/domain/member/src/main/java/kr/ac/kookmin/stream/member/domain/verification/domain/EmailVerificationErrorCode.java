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

    EMAIL_VERIFICATION_COOLDOWN(ErrorStatus.TOO_MANY_REQUESTS, "인증 메일은 1분에 한 번 보낼 수 있습니다. 잠시 후 다시 시도해 주세요."),
    EMAIL_VERIFICATION_DAILY_LIMIT_EXCEEDED(ErrorStatus.TOO_MANY_REQUESTS, "오늘 보낼 수 있는 인증 메일을 모두 보냈습니다. 내일 다시 시도해 주세요."),
    EMAIL_SEND_FAILED(ErrorStatus.BAD_GATEWAY, "인증 메일을 보내지 못했습니다. 잠시 후 다시 시도해 주세요."),
    EMAIL_VERIFICATION_NOT_FOUND(ErrorStatus.BAD_REQUEST, "이 이메일로 받은 인증 코드가 없습니다. 인증 코드를 먼저 요청해 주세요."),
    EMAIL_VERIFICATION_EXPIRED(ErrorStatus.BAD_REQUEST, "인증 코드가 만료되었습니다. 인증 코드를 다시 요청해 주세요."),
    EMAIL_VERIFICATION_CODE_MISMATCH(ErrorStatus.BAD_REQUEST, "인증 코드가 올바르지 않습니다."),
    EMAIL_VERIFICATION_ATTEMPTS_EXCEEDED(ErrorStatus.BAD_REQUEST, "인증 코드를 여러 번 틀렸습니다. 인증 코드를 다시 요청해 주세요."),
    EMAIL_NOT_VERIFIED(ErrorStatus.BAD_REQUEST, "이메일 인증을 먼저 완료해 주세요.");

    private final int status;
    private final String message;
}
