package kr.ac.kookmin.stream.auth.domain.oauth.domain;

import kr.ac.kookmin.stream.common.ErrorCode;
import kr.ac.kookmin.stream.common.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum OAuthErrorCode implements ErrorCode {

    UNSUPPORTED_OAUTH_PROVIDER(ErrorStatus.BAD_REQUEST, "지원하지 않는 로그인 방식입니다."),
    REDIRECT_URI_NOT_ALLOWED(ErrorStatus.BAD_REQUEST, "허용되지 않은 redirect URI입니다."),
    INVALID_AUTHORIZATION_CODE(ErrorStatus.UNAUTHORIZED, "로그인이 만료되었습니다. 다시 로그인해 주세요."),
    OAUTH_PROVIDER_UNAVAILABLE(ErrorStatus.BAD_GATEWAY, "로그인 서버에 연결할 수 없습니다. 잠시 후 다시 시도해 주세요.");

    private final int status;
    private final String message;
}
