package kr.ac.kookmin.stream.auth.domain.oauth.repository;

import java.util.Optional;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthAccount;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthProvider;

public interface OAuthAccountRepository {
    Optional<OAuthAccount> findByProviderAndProviderUserId(OAuthProvider provider, String providerUserId);
    OAuthAccount save(OAuthAccount oauthAccount);
}
