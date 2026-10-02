package kr.ac.kookmin.stream.db.auth;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthAccount;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthProvider;
import kr.ac.kookmin.stream.db.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "oauth_accounts",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_oauth_accounts_provider_provider_user_id",
            columnNames = {"provider", "provider_user_id"}
        ),
        @UniqueConstraint(
            name = "uk_oauth_accounts_member_id_provider",
            columnNames = {"member_id", "provider"}
        )
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthAccountJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oauth_account_id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OAuthProvider provider;

    @Column(name = "provider_user_id", nullable = false)
    private String providerUserId;

    private OAuthAccountJpaEntity(OAuthAccount oauthAccount) {
        this.id = oauthAccount.getId();
        this.memberId = oauthAccount.getMemberId();
        this.provider = oauthAccount.getProvider();
        this.providerUserId = oauthAccount.getProviderUserId();
    }

    public static OAuthAccountJpaEntity from(OAuthAccount oauthAccount) {
        return new OAuthAccountJpaEntity(oauthAccount);
    }

    public OAuthAccount toDomain() {
        return OAuthAccount.of(id, memberId, provider, providerUserId);
    }
}
