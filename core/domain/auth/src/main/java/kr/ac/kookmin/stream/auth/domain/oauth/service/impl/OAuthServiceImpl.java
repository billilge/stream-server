package kr.ac.kookmin.stream.auth.domain.oauth.service.impl;

import java.util.Optional;
import kr.ac.kookmin.stream.auth.domain.oauth.client.OAuthClient;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthAccount;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthErrorCode;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthLoginCommand;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthProvider;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthUserInfo;
import kr.ac.kookmin.stream.auth.domain.oauth.repository.OAuthAccountRepository;
import kr.ac.kookmin.stream.auth.domain.oauth.service.OAuthService;
import kr.ac.kookmin.stream.common.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class OAuthServiceImpl implements OAuthService {

    private final OAuthClientRegistry oauthClientRegistry;
    private final OAuthAccountRepository oauthAccountRepository;

    // 외부 호출만 하므로 트랜잭션을 걸지 않는다
    @Override
    public OAuthUserInfo authenticate(OAuthLoginCommand command) {
        OAuthClient client = oauthClientRegistry.get(command.provider());
        if (!client.isAllowedRedirectUri(command.redirectUri())) {
            throw new BusinessException(OAuthErrorCode.REDIRECT_URI_NOT_ALLOWED);
        }

        return client.fetchUserInfo(command);
    }

    @Override
    public Optional<Long> findMemberId(OAuthProvider provider, String providerUserId) {
        return oauthAccountRepository.findByProviderAndProviderUserId(provider, providerUserId)
            .map(OAuthAccount::getMemberId);
    }

    @Override
    @Transactional
    public void link(OAuthProvider provider, String providerUserId, Long memberId) {
        oauthAccountRepository.save(OAuthAccount.create(memberId, provider, providerUserId));
    }
}
