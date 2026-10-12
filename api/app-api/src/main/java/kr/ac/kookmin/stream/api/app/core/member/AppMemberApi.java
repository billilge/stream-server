package kr.ac.kookmin.stream.api.app.core.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.core.member.request.MemberSignUpRequest;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberErrorCode;

/**
 * 학생 앱 회원 API의 문서 명세. 구현은 {@link AppMemberController}가 맡는다.
 */
@Tag(name = "회원", description = "학생 앱 회원가입")
public interface AppMemberApi {

    /** 회원가입. 로그인 응답의 signUpRequired가 true인 회원이 전화번호와 약관 동의 여부를 등록한다. */
    @Operation(summary = "회원가입",
        description = """
            로그인한 회원의 전화번호와 약관 동의 여부를 저장한다.
            전화번호는 하이픈을 넣어도 되고 빼도 된다. 필수 약관(PRIVACY_POLICY, TERMS_OF_SERVICE)에 모두 동의해야 한다.""")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = MemberErrorCode.class, codes = {
        "MEMBER_NOT_FOUND",
        "REQUIRED_TERMS_NOT_AGREED",
        "ALREADY_SIGNED_UP",
        "PHONE_NUMBER_ALREADY_EXISTS"
    })
    ApiResponse<Void> signUp(AppApiUser apiUser, MemberSignUpRequest request);
}
