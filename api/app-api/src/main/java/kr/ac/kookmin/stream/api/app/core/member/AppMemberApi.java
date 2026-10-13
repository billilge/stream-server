package kr.ac.kookmin.stream.api.app.core.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.core.member.request.FcmTokenUpdateRequest;
import kr.ac.kookmin.stream.api.app.core.member.request.MemberSignUpRequest;
import kr.ac.kookmin.stream.api.app.core.member.request.PhoneNumberUpdateRequest;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberErrorCode;

/**
 * 학생 앱 회원 API의 문서 명세. 구현은 {@link AppMemberController}가 맡는다.
 */
@Tag(name = "회원", description = "학생 앱 회원가입·FCM 토큰 등록·전화번호 변경")
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

    /** FCM 토큰 등록. 앱이 Firebase에서 받은 등록 토큰을 로그인한 회원에게 저장한다. */
    @Operation(summary = "FCM 토큰 등록",
        description = """
            로그인한 회원의 FCM 등록 토큰을 저장한다. 로그인 직후와 토큰이 갱신될 때(onTokenRefresh) 호출한다.
            같은 토큰을 가진 다른 회원이 있으면 그 회원의 토큰은 지운다(한 기기에서 계정을 바꾼 경우).""")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"})
    ApiResponse<Void> updateFcmToken(AppApiUser apiUser, FcmTokenUpdateRequest request);

    /** 전화번호 변경. 로그인한 회원이 자기 전화번호를 바꾼다. */
    @Operation(summary = "전화번호 변경",
        description = """
            로그인한 회원의 전화번호를 바꾼다. 전화번호는 하이픈을 넣어도 되고 빼도 된다.
            지금 쓰는 번호와 같으면 바꾸지 않고 성공으로 응답한다. 다른 회원이 쓰는 번호로는 바꿀 수 없다.""")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND", "PHONE_NUMBER_ALREADY_EXISTS"})
    ApiResponse<Void> updatePhoneNumber(AppApiUser apiUser, PhoneNumberUpdateRequest request);
}
