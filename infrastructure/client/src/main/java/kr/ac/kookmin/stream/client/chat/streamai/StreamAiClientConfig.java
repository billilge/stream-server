package kr.ac.kookmin.stream.client.chat.streamai;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * {@link StreamAiChatClient}가 쓰는 RestClient 빈 설정. SSE 응답을 흘러오는 대로 읽어야 하므로 응답을 모아두지 않는
 * {@link SimpleClientHttpRequestFactory}를 쓴다.
 */
@Configuration
public class StreamAiClientConfig {

    private static final Logger log = LoggerFactory.getLogger(StreamAiClientConfig.class);

    @Bean
    public RestClient streamAiRestClient(StreamAiProperties properties) {
        // 로컬처럼 AI 서버 설정 없이도 기동은 되게 두고, 빠진 설정을 알려만 준다. 챗봇을 쓰면 실패한다
        List<String> missingSettings = properties.missingSettings();
        if (!missingSettings.isEmpty()) {
            log.warn("AI 서버 설정이 비어 있어 챗봇을 쓸 수 없습니다: {}", missingSettings);
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
