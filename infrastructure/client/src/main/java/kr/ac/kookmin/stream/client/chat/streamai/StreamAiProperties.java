package kr.ac.kookmin.stream.client.chat.streamai;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI 서버(stream-ai) 연동 설정. internalToken은 AI 서버가 백엔드를 거친 요청인지 확인하는 값이라 환경 변수로만 받는다.
 * <p>
 * readTimeout은 전체 답변 시간이 아니라 조각 하나를 기다리는 시간이다. 전체 길이에 맞춰 길게 잡으면 AI 서버가 멈췄을 때도 그만큼 기다린다.
 */
@ConfigurationProperties(prefix = "chat.stream-ai")
public record StreamAiProperties(
    String baseUrl,
    String chatPath,
    String internalToken,
    Duration connectTimeout,
    Duration readTimeout
) {

    /** 비어 있는 필수 설정의 키 이름. 로그에 남기므로 값은 담지 않는다. */
    public List<String> missingSettings() {
        Map<String, String> required = new LinkedHashMap<>();
        required.put("base-url", baseUrl);
        required.put("chat-path", chatPath);
        required.put("internal-token", internalToken);

        return required.entrySet().stream()
            .filter(entry -> entry.getValue() == null || entry.getValue().isBlank())
            .map(Map.Entry::getKey)
            .toList();
    }
}
