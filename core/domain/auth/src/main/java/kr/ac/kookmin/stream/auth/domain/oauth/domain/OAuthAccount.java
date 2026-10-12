package kr.ac.kookmin.stream.auth.domain.oauth.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * provider 계정과 회원의 연결. 한 회원은 provider마다 계정을 하나씩 연결할 수 있다.
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OAuthAccount {

    private Long id;
    private Long memberId;
    private OAuthProvider provider;
    private String providerUserId;

    public static OAuthAccount create(Long memberId, OAuthProvider provider, String providerUserId) {
        return new OAuthAccount(null, memberId, provider, providerUserId);
    }

    public static OAuthAccount of(Long id, Long memberId, OAuthProvider provider, String providerUserId) {
        return new OAuthAccount(id, memberId, provider, providerUserId);
    }
}
