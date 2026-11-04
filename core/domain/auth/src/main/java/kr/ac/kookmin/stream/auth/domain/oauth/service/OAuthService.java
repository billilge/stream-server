package kr.ac.kookmin.stream.auth.domain.oauth.service;

import java.util.Optional;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthLoginCommand;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthProvider;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthUserInfo;

public interface OAuthService {
    OAuthUserInfo authenticate(OAuthLoginCommand command);
    Optional<Long> findMemberId(OAuthProvider provider, String providerUserId);
    void link(OAuthProvider provider, String providerUserId, Long memberId);
    /** 회원에게 연결된 provider 계정을 모두 끊는다. 탈퇴 뒤 같은 계정으로 로그인하면 새 회원으로 가입된다. */
    void unlinkAll(Long memberId);
}
