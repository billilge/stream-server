package kr.ac.kookmin.stream.auth.domain.oauth.service.impl;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.auth.domain.oauth.client.OAuthClient;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthErrorCode;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthProvider;
import kr.ac.kookmin.stream.common.BusinessException;
import org.springframework.stereotype.Component;

/**
 * 빈으로 등록된 OAuthClient 구현체를 provider별로 모아 두고, 요청의 provider에 맞는 구현체를 돌려준다.
 * 같은 provider의 구현체가 둘이면 기동 시 실패한다.
 */
@Component
class OAuthClientRegistry {

    private final Map<OAuthProvider, OAuthClient> clients;

    OAuthClientRegistry(List<OAuthClient> clients) {
        this.clients = clients.stream()
            .collect(Collectors.toMap(
                OAuthClient::provider,
                Function.identity(),
                (first, second) -> {
                    throw new IllegalStateException("OAuthClient 구현체가 중복됐습니다: " + first.provider());
                },
                () -> new EnumMap<>(OAuthProvider.class)
            ));
    }

    OAuthClient get(OAuthProvider provider) {
        OAuthClient client = clients.get(provider);
        if (client == null) {
            throw new BusinessException(OAuthErrorCode.UNSUPPORTED_OAUTH_PROVIDER);
        }
        return client;
    }
}
