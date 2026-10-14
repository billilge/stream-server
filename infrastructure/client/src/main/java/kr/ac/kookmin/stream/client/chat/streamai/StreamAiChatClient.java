package kr.ac.kookmin.stream.client.chat.streamai;

import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.chat.client.ChatClient;
import kr.ac.kookmin.stream.welfare.domain.chat.client.ChatEventListener;
import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatTurn;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * AI 서버(stream-ai)에 질문을 보내고 답변을 SSE로 받아 넘긴다.
 * <p>
 * 응답을 객체로 변환하지 않고 {@code exchange} 안에서 본문을 흘러오는 대로 읽는다. 변환을 맡기면 전체가 도착할 때까지 기다리게 되어
 * 스트리밍이 사라진다.
 */
@Component
@RequiredArgsConstructor
public class StreamAiChatClient implements ChatClient {

    private static final Logger log = LoggerFactory.getLogger(StreamAiChatClient.class);

    /** 학생에게 보여줄 실패 안내. 내부 원인은 로그에만 남긴다. */
    private static final String FALLBACK_ERROR_MESSAGE = "답변 생성 중 오류가 발생했습니다.";

    private static final String EVENT_DELTA = "delta";
    private static final String EVENT_BUBBLE = "bubble";
    private static final String EVENT_DONE = "done";
    private static final String EVENT_ERROR = "error";

    @Qualifier("streamAiRestClient")
    private final RestClient streamAiRestClient;

    private final StreamAiProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    public void stream(long memberId, List<ChatTurn> history, ChatEventListener listener) {
        TerminalGuard guard = TerminalGuard.from(listener);

        try {
            streamAiRestClient.post()
                .uri(properties.chatPath())
                .header("X-Internal-Token", properties.internalToken())
                .header("X-User-Id", String.valueOf(memberId))
                .accept(MediaType.TEXT_EVENT_STREAM)
                .body(StreamAiChatRequest.from(history))
                .exchange((request, response) -> {
                    if (response.getStatusCode().isError()) {
                        // 질문 원문은 남기지 않는다. 학생의 개인 사정이 섞여 들어올 수 있다
                        log.error("AI 서버가 오류를 돌려줬습니다. status={}", response.getStatusCode());
                        guard.onError(FALLBACK_ERROR_MESSAGE);
                        return null;
                    }
                    SseEventReader.read(response.getBody(), event -> dispatch(event, guard));
                    return null;
                });
        } catch (RestClientException e) {
            // 리스너가 던진 예외(학생 연결 끊김)는 AI 서버 실패가 아니므로 잡지 않고 그대로 올려 보낸다
            log.error("AI 서버 호출에 실패했습니다.", e);
            guard.onError(FALLBACK_ERROR_MESSAGE);
        }

        // done도 error도 없이 스트림이 끊긴 경우. 이미 끝났으면 무시된다
        guard.onError(FALLBACK_ERROR_MESSAGE);
    }

    private void dispatch(SseEvent event, TerminalGuard guard) {
        switch (event.name()) {
            case EVENT_DELTA -> {
                String text = readField(event.data(), "text");
                if (text != null && !text.isEmpty()) {
                    guard.onDelta(text);
                }
            }
            case EVENT_BUBBLE -> guard.onBubble();
            case EVENT_DONE -> guard.onDone();
            case EVENT_ERROR -> {
                String message = readField(event.data(), "message");
                guard.onError(message == null || message.isBlank() ? FALLBACK_ERROR_MESSAGE : message);
            }
            // 계약에 없는 이벤트가 추가되어도 중계가 멈추지 않게 한다
            default -> log.debug("모르는 SSE 이벤트를 건너뜁니다. event={}", event.name());
        }
    }

    /** JSON 한 줄에서 필드 하나를 꺼낸다. 깨진 줄 하나가 대화 전체를 끊지 않도록 파싱 실패는 삼킨다. */
    private String readField(String data, String field) {
        if (data == null || data.isBlank()) {
            return null;
        }
        try {
            JsonNode node = objectMapper.readTree(data).get(field);
            return node == null || node.isNull() ? null : node.asString();
        } catch (JacksonException e) {
            log.debug("SSE data를 읽지 못해 건너뜁니다. field={}", field);
            return null;
        }
    }
}
