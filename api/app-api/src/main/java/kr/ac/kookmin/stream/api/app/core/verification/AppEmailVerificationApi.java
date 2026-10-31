package kr.ac.kookmin.stream.api.app.core.verification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.core.verification.request.EmailVerificationConfirmRequest;
import kr.ac.kookmin.stream.api.app.core.verification.request.EmailVerificationSendRequest;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberErrorCode;
import kr.ac.kookmin.stream.member.domain.verification.domain.EmailVerificationErrorCode;

/**
 * 학생 앱 이메일 인증 API의 문서 명세. 구현은 {@link AppEmailVerificationController}가 맡는다.
 */
@Tag(name = "이메일 인증", description = "회원가입 전 이메일 인증 코드 발송·확인")
public interface AppEmailVerificationApi {

    /** 인증 코드 발송. */
    @Operation(summary = "이메일 인증 코드 발송",
        description = """
            로그인한 회원이 입력한 이메일로 6자리 인증 코드를 보낸다. 코드는 5분 동안 유효하다.
            다시 보내려면 1분을 기다려야 하고, 하루(자정 기준)에 10번까지 보낼 수 있다.
            새 코드를 받으면 앞서 받은 코드는 쓸 수 없다. 다른 회원이 쓰는 이메일로는 보낼 수 없다.
            메일은 응답한 뒤에 보내므로 성공 응답이 메일 도착을 보장하지 않는다. 메일이 오지 않으면 1분 뒤 다시 요청한다.
            EMAIL_SEND_FAILED는 발송 요청이 몰려 받지 못했을 때 응답한다.""")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND", "EMAIL_ALREADY_EXISTS"})
    @ApiErrorCode(type = EmailVerificationErrorCode.class, codes = {
        "EMAIL_VERIFICATION_COOLDOWN",
        "EMAIL_VERIFICATION_DAILY_LIMIT_EXCEEDED",
        "EMAIL_SEND_FAILED"
    })
    ApiResponse<Void> sendCode(AppApiUser apiUser, EmailVerificationSendRequest request);

    /** 인증 코드 확인. */
    @Operation(summary = "이메일 인증 코드 확인",
        description = """
            가장 최근에 받은 인증 코드를 확인한다. 이메일은 코드를 요청할 때와 같아야 한다.
            코드를 5번 틀리면 코드를 다시 요청해야 한다. 이미 인증한 요청을 다시 보내면 성공으로 응답한다.
            인증한 뒤 30분 안에 회원가입을 마쳐야 한다.""")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = EmailVerificationErrorCode.class, codes = {
        "EMAIL_VERIFICATION_NOT_FOUND",
        "EMAIL_VERIFICATION_EXPIRED",
        "EMAIL_VERIFICATION_CODE_MISMATCH",
        "EMAIL_VERIFICATION_ATTEMPTS_EXCEEDED"
    })
    ApiResponse<Void> confirm(AppApiUser apiUser, EmailVerificationConfirmRequest request);
}
