package kr.ac.kookmin.stream.api.app.core.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.app.core.auth.request.OAuthLoginRequest;
import kr.ac.kookmin.stream.api.app.core.auth.response.OAuthLoginResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberErrorCode;

/**
 * 로그인 API의 문서 명세. 구현은 {@link AppAuthController}가 맡는다.
 * 학생 앱과 운영진 콘솔이 같이 쓰며, 접근 범위는 발급된 토큰의 role로 나뉜다.
 */
@Tag(name = "인증", description = "OAuth 로그인")
public interface AppAuthApi {

    /** provider 로그인. 앱·웹이 PKCE 로그인으로 받은 code를 서비스 토큰으로 바꾼다. */
    @Operation(summary = "OAuth 로그인",
        description = """
            provider(현재 kconnect)에서 받은 code·codeVerifier·redirectUri로 로그인한다.
            처음 로그인하면 회원이 만들어진다. termsAgreementRequired가 true면 필수 약관 동의가 필요하다.""")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = OAuthErrorCode.class, codes = {
        "UNSUPPORTED_OAUTH_PROVIDER",
        "REDIRECT_URI_NOT_ALLOWED",
        "INVALID_AUTHORIZATION_CODE",
        "OAUTH_PROVIDER_UNAVAILABLE"
    })
    @ApiErrorCode(type = MemberErrorCode.class, codes = {"DEPARTMENT_NOT_ALLOWED"})
    ApiResponse<OAuthLoginResponse> login(String provider, OAuthLoginRequest request);
}
