package kr.ac.kookmin.stream.auth.domain.oauth.service;

import java.util.Optional;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthLoginCommand;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthProvider;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthUserInfo;

public interface OAuthService {
    OAuthUserInfo authenticate(OAuthLoginCommand command);
    Optional<Long> findMemberId(OAuthProvider provider, String providerUserId);
    void link(OAuthProvider provider, String providerUserId, Long memberId);
}
