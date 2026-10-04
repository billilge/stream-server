package kr.ac.kookmin.stream.client.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
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

/**
 * AI 서버(stream-ai)에 질문을 보내고 답변을 SSE로 받아 넘긴다.
 * <p>
 * 다른 외부 클라이언트와 다른 점이 하나 있다. 응답을 객체로 변환해 돌려받지 않고
 * <b>본문을 흘러오는 대로 읽는다.</b> {@code RestClient}의 {@code exchange}는 응답을 변환하지 않고
 * 그대로 넘겨주므로 그 안에서 입력 스트림을 직접 읽는다. 변환을 맡기면 전체가 도착할 때까지
 * 기다리게 되어 스트리밍이 사라진다.
 */
@Component
@RequiredArgsConstructor
public class StreamAiChatClient implements ChatClient {

    private static final Logger log = LoggerFactory.getLogger(StreamAiChatClient.class);

    /** 학생에게 보여줄 실패 안내. 내부 원인은 로그에만 남긴다. */
    private static final String FALLBACK_ERROR_MESSAGE = "답변 생성 중 오류가 발생했습니다.";

    private static final String EVENT_PREFIX = "event:";
    private static final String DATA_PREFIX = "data:";

    private static final String EVENT_DELTA = "delta";
    private static final String EVENT_BUBBLE = "bubble";
    private static final String EVENT_DONE = "done";
    private static final String EVENT_ERROR = "error";

    @Qualifier("streamAiRestClient")
    private final RestClient streamAiRestClient;

    private final StreamAiProperties properties;

    /**
     * 빈을 주입받지 않고 직접 만든다. 스프링이 만들어 주는 매퍼는 Jackson 3(tools.jackson)인데
     * 이 모듈이 선언한 의존성은 Jackson 2(com.fasterxml)다. 여기서 하는 일은 고정된 모양의
     * JSON 한 줄에서 필드 하나를 꺼내는 것뿐이라 앱의 직렬화 설정과 무관하다.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void stream(long memberId, List<ChatTurn> history, ChatEventListener listener) {
        // 종료 이벤트를 두 번 보내지 않도록 한 곳에서 관리한다. 학생 쪽 연결을 두 번 닫으면 예외가 난다.
        TerminalGuard guard = new TerminalGuard(listener);

        try {
            streamAiRestClient.post()
                .uri(properties.chatPath())
                .header("X-Internal-Token", properties.internalToken())
                .header("X-User-Id", String.valueOf(memberId))
                .accept(MediaType.TEXT_EVENT_STREAM)
                .body(toRequestBody(history))
                .exchange((request, response) -> {
                    if (response.getStatusCode().isError()) {
                        // 질문 원문은 남기지 않는다. 학생의 개인 사정이 섞여 들어올 수 있다.
                        log.error("AI 서버가 오류를 돌려줬습니다. status={}", response.getStatusCode());
                        guard.error(FALLBACK_ERROR_MESSAGE);
                        return null;
                    }
                    readEvents(response.getBody(), guard);
                    return null;
                }, false);
        } catch (Exception e) {
            log.error("AI 서버 호출에 실패했습니다.", e);
            guard.error(FALLBACK_ERROR_MESSAGE);
        }

        // 스트림이 done도 error도 없이 끊긴 경우. 이게 없으면 학생 쪽 연결이 영원히 열려 있다.
        guard.errorIfNotFinished(FALLBACK_ERROR_MESSAGE);
    }

    /** AI 서버 계약: {@code { "messages": [ { "role": ..., "text": ... } ] }} */
    private Map<String, Object> toRequestBody(List<ChatTurn> history) {
        List<Map<String, String>> messages = history.stream()
            .map(turn -> Map.of("role", turn.role(), "text", turn.text()))
            .toList();
        return Map.of("messages", messages);
    }

    /**
     * SSE 본문을 줄 단위로 읽는다. 형식은 {@code event: 이름}, {@code data: JSON}, 빈 줄 한 번이다.
     * <p>
     * 빈 줄이 이벤트 하나의 끝이다. {@code data}의 JSON은 줄바꿈이 {@code \\n}으로 escape되어 들어오므로
     * 한 줄을 넘지 않는다. 그래서 줄 단위로 읽어도 쪼개지지 않는다.
     */
    private void readEvents(InputStream body, TerminalGuard guard) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(body, StandardCharsets.UTF_8))) {
            String event = null;
            String data = null;

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    dispatch(event, data, guard);
                    event = null;
                    data = null;
                    continue;
                }
                if (line.startsWith(EVENT_PREFIX)) {
                    event = line.substring(EVENT_PREFIX.length()).trim();
                } else if (line.startsWith(DATA_PREFIX)) {
                    data = line.substring(DATA_PREFIX.length()).trim();
                }
                // 그 외 줄(주석 등)은 무시한다. 계약에 없지만 와도 깨지지 않아야 한다.
            }

            // 마지막 이벤트 뒤에 빈 줄이 없이 끊긴 경우를 처리한다.
            dispatch(event, data, guard);
        }
    }

    private void dispatch(String event, String data, TerminalGuard guard) {
        if (event == null) {
            return;
        }
        switch (event) {
            case EVENT_DELTA -> {
                String text = readField(data, "text");
                if (text != null && !text.isEmpty()) {
                    guard.delta(text);
                }
            }
            case EVENT_BUBBLE -> guard.bubble();
            case EVENT_DONE -> guard.done();
            case EVENT_ERROR -> {
                String message = readField(data, "message");
                guard.error(message == null || message.isBlank() ? FALLBACK_ERROR_MESSAGE : message);
            }
            // 계약에 없는 이벤트가 추가되어도 중계가 멈추지 않게 한다.
            default -> log.debug("모르는 SSE 이벤트를 건너뜁니다. event={}", event);
        }
    }

    /** JSON 한 줄에서 필드 하나를 꺼낸다. 깨진 줄 하나가 대화 전체를 끊지 않도록 예외를 삼킨다. */
    private String readField(String data, String field) {
        if (data == null || data.isBlank()) {
            return null;
        }
        try {
            JsonNode node = objectMapper.readTree(data).get(field);
            return node == null || node.isNull() ? null : node.asText();
        } catch (Exception e) {
            log.debug("SSE data를 읽지 못해 건너뜁니다. field={}", field);
            return null;
        }
    }

    /**
     * 종료 이벤트가 한 번만 나가도록 감싼다.
     * <p>
     * {@code done}과 {@code error}는 둘 중 하나만, 한 번만 불려야 한다. 학생 쪽 연결을
     * 두 번 닫으려 하면 예외가 나고, 한 번도 닫지 않으면 연결이 계속 열려 있다.
     */
    private static final class TerminalGuard implements ChatEventListener {

        private final ChatEventListener delegate;
        private boolean finished;

        private TerminalGuard(ChatEventListener delegate) {
            this.delegate = delegate;
        }

        @Override
        public void onDelta(String text) {
            delta(text);
        }

        @Override
        public void onBubble() {
            bubble();
        }

        @Override
        public void onDone() {
            done();
        }

        @Override
        public void onError(String message) {
            error(message);
        }

        void delta(String text) {
            if (!finished) {
                delegate.onDelta(text);
            }
        }

        void bubble() {
            if (!finished) {
                delegate.onBubble();
            }
        }

        void done() {
            if (finished) {
                return;
            }
            finished = true;
            delegate.onDone();
        }

        void error(String message) {
            if (finished) {
                return;
            }
            finished = true;
            delegate.onError(message);
        }

        void errorIfNotFinished(String message) {
            error(message);
        }
    }
}
