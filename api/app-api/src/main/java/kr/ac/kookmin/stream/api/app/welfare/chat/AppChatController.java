package kr.ac.kookmin.stream.api.app.welfare.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.welfare.chat.request.ChatMessageCreateRequest;
import kr.ac.kookmin.stream.welfare.domain.chat.client.ChatEventListener;
import kr.ac.kookmin.stream.welfare.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/v1/app/chat")
@RequiredArgsConstructor
public class AppChatController implements AppChatApi {

    private static final Logger log = LoggerFactory.getLogger(AppChatController.class);

    private final ChatService chatService;
    private final ObjectMapper objectMapper;

    /**
     * {@code SseEmitter} 대신 {@link StreamingResponseBody}를 쓴다.
     * <p>
     * {@code SseEmitter}는 먼저 돌려준 뒤 다른 스레드에서 이벤트를 넣는 방식이라 스레드 풀을 따로
     * 관리해야 하고, 완료 처리를 빠뜨리면 연결이 남는다. {@code StreamingResponseBody}는 스프링이
     * 콜백을 비동기 스레드에서 돌려주므로 그 안에서 순서대로 쓰면 되고, 콜백이 끝나면 연결도 닫힌다.
     * <p>
     * 대신 SSE 형식을 직접 쓴다. 어차피 AI 서버가 보낸 형식을 그대로 중계하는 일이라
     * 프레임워크가 다시 조립할 이유가 없다.
     */
    @Override
    @PostMapping(value = "/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<StreamingResponseBody> sendMessage(
        AppApiUser apiUser,
        @Valid @RequestBody ChatMessageCreateRequest request
    ) {
        long memberId = apiUser.userId();

        StreamingResponseBody body = output -> {
            SseWriter writer = new SseWriter(output, objectMapper);
            try {
                chatService.answer(memberId, request.message(), writer);
            } catch (UncheckedIOException e) {
                // 학생이 화면을 닫으면 쓰기가 실패한다. 오류가 아니라 정상 종료다.
                log.debug("학생이 연결을 끊어 중계를 멈춥니다. memberId={}", memberId);
            }
        };

        return ResponseEntity.ok()
            // 아래 두 헤더는 중간에 끼는 프록시가 응답을 모아두지 못하게 막는다.
            // 모아두면 조각이 한 번에 도착해 스트리밍이 사라진다. AI 서버도 같은 헤더를 보낸다.
            .header(HttpHeaders.CACHE_CONTROL, "no-cache")
            .header("X-Accel-Buffering", "no")
            .contentType(MediaType.TEXT_EVENT_STREAM)
            .body(body);
    }

    /**
     * 받은 이벤트를 학생 쪽 연결에 SSE 형식으로 쓴다.
     * <p>
     * 형식은 {@code event: 이름}, {@code data: JSON}, 빈 줄 한 번이다. 조각마다 즉시 flush 한다.
     * 모아두면 학생이 10초 동안 빈 화면을 본다.
     */
    private static final class SseWriter implements ChatEventListener {

        private final OutputStream output;
        private final ObjectMapper objectMapper;

        private SseWriter(OutputStream output, ObjectMapper objectMapper) {
            this.output = output;
            this.objectMapper = objectMapper;
        }

        @Override
        public void onDelta(String text) {
            write("delta", Map.of("text", text));
        }

        @Override
        public void onBubble() {
            write("bubble", Map.of());
        }

        @Override
        public void onDone() {
            write("done", Map.of());
        }

        @Override
        public void onError(String message) {
            write("error", Map.of("message", message));
        }

        private void write(String event, Map<String, ?> data) {
            try {
                String frame = "event: " + event + "\ndata: " + objectMapper.writeValueAsString(data) + "\n\n";
                output.write(frame.getBytes(StandardCharsets.UTF_8));
                output.flush();
            } catch (IOException e) {
                // 서비스까지 올려 보내 중계를 멈춘다. 받을 사람이 없는데 계속 만들 이유가 없다.
                throw new UncheckedIOException(e);
            }
        }
    }
}
