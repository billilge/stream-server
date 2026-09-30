package kr.ac.kookmin.stream.db.auth;

import java.util.Optional;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthAccount;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthProvider;
import kr.ac.kookmin.stream.auth.domain.oauth.repository.OAuthAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OAuthAccountRepositoryImpl implements OAuthAccountRepository {

    private final OAuthAccountJpaRepository oauthAccountJpaRepository;

    @Override
    public Optional<OAuthAccount> findByProviderAndProviderUserId(OAuthProvider provider, String providerUserId) {
        return oauthAccountJpaRepository.findByProviderAndProviderUserId(provider, providerUserId)
            .map(OAuthAccountJpaEntity::toDomain);
    }

    @Override
    public OAuthAccount save(OAuthAccount oauthAccount) {
        return oauthAccountJpaRepository.save(OAuthAccountJpaEntity.from(oauthAccount)).toDomain();
    }
}
