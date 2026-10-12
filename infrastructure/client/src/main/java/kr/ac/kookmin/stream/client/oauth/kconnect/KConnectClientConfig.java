package kr.ac.kookmin.stream.client.oauth.kconnect;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * {@link KConnectOAuthClient}가 쓰는 RestClient 빈 설정. 로그인 요청이 KConnect 응답을 기다리며 오래 묶이지 않도록 타임아웃을 건다.
 */
@Configuration
public class KConnectClientConfig {

    private static final Logger log = LoggerFactory.getLogger(KConnectClientConfig.class);

    @Bean
    public RestClient kconnectRestClient(KConnectProperties properties) {
        // 로컬처럼 KConnect 설정 없이도 기동은 되게 두고, 빠진 설정을 알려만 준다. 로그인하면 500이 난다
        List<String> missingSettings = properties.missingSettings();
        if (!missingSettings.isEmpty()) {
            log.warn("KConnect 설정이 비어 있어 KConnect 로그인을 쓸 수 없습니다: {}", missingSettings);
        }

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.connectTimeout());
        requestFactory.setReadTimeout(properties.readTimeout());

        return RestClient.builder()
            .baseUrl(properties.baseUrl())
            .requestFactory(requestFactory)
            .build();
    }
}
