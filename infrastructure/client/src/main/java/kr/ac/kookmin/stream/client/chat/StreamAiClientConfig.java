package kr.ac.kookmin.stream.client.chat;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * {@link StreamAiChatClient}가 쓰는 RestClient 빈 설정.
 * <p>
 * 응답 본문을 통째로 읽지 않고 흘러오는 대로 읽어야 하므로, 버퍼링을 하는 요청 팩터리를 쓰지 않는다.
 * {@link SimpleClientHttpRequestFactory}는 응답을 모아두지 않아 SSE에 쓸 수 있다.
 */
@Configuration
public class StreamAiClientConfig {

    private static final Logger log = LoggerFactory.getLogger(StreamAiClientConfig.class);

    @Bean
    public RestClient streamAiRestClient(StreamAiProperties properties) {
        // 로컬에서 AI 서버 없이도 기동은 되게 두고, 빠진 설정만 알려준다. 챗봇을 쓰면 실패한다
        List<String> missingSettings = properties.missingSettings();
        if (!missingSettings.isEmpty()) {
            log.warn("AI 서버 설정이 비어 있어 챗봇을 쓸 수 없습니다: {}", missingSettings);
        }

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.connectTimeout());
        requestFactory.setReadTimeout(properties.readTimeout());

        return RestClient.builder()
            .baseUrl(properties.baseUrl() == null ? "" : properties.baseUrl())
            .requestFactory(requestFactory)
            .build();
    }
}
