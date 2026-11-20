package kr.ac.kookmin.stream.security.aiserver;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI 서버(stream-ai)가 {@code /v1/ai/**}를 부를 때 싣는 내부 토큰.
 * <p>
 * 백엔드 → AI 서버 방향에 쓰는 {@code STREAM_AI_INTERNAL_TOKEN}과 같은 값을 쓴다. 비밀값 하나로 양방향을 맞춰
 * 배포 설정을 단순하게 둔다. 비어 있으면 {@code /v1/ai/**}는 전부 401이다(설정 누락으로 열리지 않게).
 *
 * @param internalToken {@code X-Internal-Token} 헤더와 비교할 값
 */
@ConfigurationProperties(prefix = "ai-server")
public record AiServerProperties(
    String internalToken
) {

    public boolean hasToken() {
        return internalToken != null && !internalToken.isBlank();
    }
}
