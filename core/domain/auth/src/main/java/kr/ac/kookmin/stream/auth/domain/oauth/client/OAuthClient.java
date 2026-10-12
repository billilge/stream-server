package kr.ac.kookmin.stream.auth.domain.oauth.client;

import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthLoginCommand;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthProvider;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthUserInfo;

/**
 * provider별 OAuth 로그인 전략. 구현체는 provider마다 하나씩 모두 빈으로 등록하고,
 * 요청의 provider에 맞는 구현체를 OAuthClientRegistry가 고른다.
 */
public interface OAuthClient {

    OAuthProvider provider();

    /** provider에 등록한 redirect URI와 정확히 일치하는지. 외부 호출 전에 거르는 용도다. */
    boolean isAllowedRedirectUri(String redirectUri);

    /**
     * code를 provider 토큰으로 교환하고 사용자 정보를 조회한다.
     * provider 토큰은 이 메서드 안에서만 쓰고 밖으로 내보내지 않는다.
     */
    OAuthUserInfo fetchUserInfo(OAuthLoginCommand command);
}
