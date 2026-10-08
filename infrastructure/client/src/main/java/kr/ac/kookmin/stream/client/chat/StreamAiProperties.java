package kr.ac.kookmin.stream.client.chat;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI 서버(stream-ai) 연동 설정.
 * <p>
 * {@code internalToken}은 AI 서버가 "백엔드를 거친 요청"만 처리하기 위해 확인하는 값이다.
 * 코드에 두지 않고 환경 변수로만 받는다.
 * <p>
 * {@code readTimeout}은 <b>조각 하나를 기다리는 시간</b>이고 전체 응답 시간이 아니다.
 * SSE는 연결을 열어 둔 채 조각을 계속 보내므로, 조각 사이 간격보다 넉넉하면 된다.
 * 전체 길이에 맞춰 길게 잡으면 AI 서버가 멈췄을 때도 그만큼 기다린다.
 */
@ConfigurationProperties(prefix = "client.stream-ai")
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
