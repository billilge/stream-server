package kr.ac.kookmin.stream.client.oauth.kconnect;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import kr.ac.kookmin.stream.auth.domain.oauth.client.OAuthClient;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthErrorCode;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthLoginCommand;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthProvider;
import kr.ac.kookmin.stream.auth.domain.oauth.domain.OAuthUserInfo;
import kr.ac.kookmin.stream.common.BusinessException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

/**
 * KConnect(국민대 학생용 OAuth) 구현체. 앱·웹이 PKCE 로그인으로 받은 code를 access token으로 교환하고,
 * 그 토큰으로 사용자 정보 API를 불러 사용자를 확인한다. access token은 사용자 조회에만 쓰고 저장하지 않는다.
 * <p>
 * KConnect 측 요청으로 API 명세는 코드에 두지 않는다. 경로와 응답 필드 이름은 {@link KConnectProperties}로 받는다.
 * code·code verifier·access token·client secret은 로그와 예외 메시지에 남기지 않는다.
 */
@Component
@RequiredArgsConstructor
public class KConnectOAuthClient implements OAuthClient {

    private static final Logger log = LoggerFactory.getLogger(KConnectOAuthClient.class);

    // RFC 6749 표준 에러 코드: code 만료·재사용·verifier 불일치
    private static final String INVALID_GRANT = "invalid_grant";
    private static final ParameterizedTypeReference<Map<String, Object>> JSON_OBJECT = new ParameterizedTypeReference<>() {};

    private final KConnectProperties properties;

    @Qualifier("kconnectRestClient")
    private final RestClient kconnectRestClient;

    @Override
    public OAuthProvider provider() {
        return OAuthProvider.KCONNECT;
    }

    @Override
    public boolean isAllowedRedirectUri(String redirectUri) {
        return properties.allowedRedirectUris().contains(redirectUri);
    }

    @Override
    public OAuthUserInfo fetchUserInfo(OAuthLoginCommand command) {
        List<String> missingSettings = properties.missingSettings();
        if (!missingSettings.isEmpty()) {
            throw new IllegalStateException("KConnect 설정이 비어 있습니다: " + missingSettings);
        }

        String accessToken = exchangeCode(command);
        return toOAuthUserInfo(requestUserInfo(accessToken));
    }

    // code는 한 번만 쓸 수 있어 두 번째 요청은 항상 invalid_grant가 된다. 그래서 재시도하지 않는다
    private String exchangeCode(OAuthLoginCommand command) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", command.code());
        form.add("redirect_uri", command.redirectUri());
        form.add("code_verifier", command.codeVerifier());
        form.add("client_id", properties.clientId());
        form.add("client_secret", properties.clientSecret());

        try {
            KConnectTokenResponse response = kconnectRestClient.post()
                .uri(properties.tokenPath())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(KConnectTokenResponse.class);
            if (response == null || response.accessToken() == null) {
                throw new IllegalStateException("KConnect 토큰 응답에 access_token이 없습니다");
            }
            return response.accessToken();
        } catch (HttpClientErrorException e) {
            // 사용자가 다시 로그인하면 되는 경우
            if (e.getResponseBodyAsString().contains(INVALID_GRANT)) {
                throw new BusinessException(OAuthErrorCode.INVALID_AUTHORIZATION_CODE);
            }
            // invalid_client 등: Client ID·Secret 설정 오류라 서버에서 알아야 하므로 500으로 남긴다
            throw new IllegalStateException(
                "KConnect 토큰 교환 실패: " + e.getStatusCode() + " " + e.getResponseBodyAsString(), e);
        } catch (HttpServerErrorException | ResourceAccessException e) {
            throw providerUnavailable(e);
        }
    }

    private Map<String, Object> requestUserInfo(String accessToken) {
        try {
            Map<String, Object> response = kconnectRestClient.get()
                .uri(properties.userInfo().path())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(JSON_OBJECT);
            if (response == null) {
                throw new IllegalStateException("KConnect 사용자 정보 응답이 비어 있습니다");
            }
            return response;
        } catch (HttpClientErrorException e) {
            // 방금 발급받은 토큰이 거절되면 scope·경로 등 설정 문제다
            throw new IllegalStateException("KConnect 사용자 정보 조회 실패: " + e.getStatusCode(), e);
        } catch (HttpServerErrorException | ResourceAccessException e) {
            throw providerUnavailable(e);
        }
    }

    private OAuthUserInfo toOAuthUserInfo(Map<String, Object> response) {
        KConnectProperties.UserInfo fields = properties.userInfo();
        String providerUserId = field(response, fields.idField());
        if (providerUserId == null) {
            throw new IllegalStateException("KConnect 사용자 정보 응답에 고유 ID가 없습니다");
        }

        return new OAuthUserInfo(
            OAuthProvider.KCONNECT,
            providerUserId,
            field(response, fields.studentIdField()),
            field(response, fields.nameField()),
            field(response, fields.majorField()),
            field(response, fields.academicStatusField())
        );
    }

    private static String field(Map<String, Object> response, String name) {
        return Objects.toString(response.get(name), null);
    }

    private BusinessException providerUnavailable(Exception e) {
        log.warn("KConnect 호출 실패: {}", e.getMessage());
        return new BusinessException(OAuthErrorCode.OAUTH_PROVIDER_UNAVAILABLE);
    }
}
