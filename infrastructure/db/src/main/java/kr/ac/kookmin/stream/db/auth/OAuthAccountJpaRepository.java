package kr.ac.kookmin.stream.db.auth;

import java.util.Optional;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthAccountJpaRepository extends JpaRepository<OAuthAccountJpaEntity, Long> {
    Optional<OAuthAccountJpaEntity> findByProviderAndProviderUserId(OAuthProvider provider, String providerUserId);
}
