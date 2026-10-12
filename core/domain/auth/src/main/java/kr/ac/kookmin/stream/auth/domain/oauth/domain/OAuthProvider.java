package kr.ac.kookmin.stream.auth.domain.oauth.domain;

import java.util.Arrays;
import kr.ac.kookmin.stream.common.BusinessException;

public enum OAuthProvider {
    KCONNECT;

    /** 로그인 경로의 provider 값(예: "kconnect")을 대소문자 구분 없이 변환한다. */
    public static OAuthProvider from(String value) {
        return Arrays.stream(values())
            .filter(provider -> provider.name().equalsIgnoreCase(value))
            .findFirst()
            .orElseThrow(() -> new BusinessException(OAuthErrorCode.UNSUPPORTED_OAUTH_PROVIDER));
    }
}
