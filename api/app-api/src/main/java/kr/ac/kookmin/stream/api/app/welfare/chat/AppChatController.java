package kr.ac.kookmin.stream.api.app.welfare.chat;

import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/v1/app/chat")
@RequiredArgsConstructor
public class AppChatController implements AppChatApi {

    private static final Logger log = LoggerFactory.getLogger(AppChatController.class);

    private final ChatService chatService;

    /** 스프링 부트 4가 만들어 주는 매퍼(Jackson 3). 앱 전체가 쓰는 것과 같은 설정을 따른다. */
    private final ObjectMapper objectMapper;

    /**
     * 응답 스트림에 <b>직접</b> 쓴다. {@code StreamingResponseBody}도 {@code SseEmitter}도 쓰지 않는다.
     * <p>
     * {@code StreamingResponseBody}로 먼저 만들었는데 조각이 흘러가지 않았다. 300ms 간격으로
     * 다섯 번 쓰고 매번 {@code flush()}해도 클라이언트는 한 번에 받았고, 조각을 4KB로 키우자
     * <b>8KB 경계에서만</b> 끊겨 나갔다. 즉 {@code flush()}가 무시되고 톰캣 버퍼가 찰 때만
     * 나간다는 뜻이다. 같은 내용을 이 방식으로 쓰면 300ms 간격 그대로 도착한다(아래 측정값).
     * <p>
     * 대가가 있다. 비동기가 아니므로 답변이 끝날 때까지 요청 스레드를 쥐고 있는다. 톰캣 기본
     * 최대 스레드가 200이라 동시 대화 200건까지는 버티지만, 그보다 늘어나면 다른 요청이 밀린다.
     * 지금 규모에서는 문제가 아니고, 늘어나면 {@code SseEmitter}를 다시 시험해 볼 자리다.
     */
    @Override
    @PostMapping(value = "/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public void sendMessage(
        AppApiUser apiUser,
        @Valid @RequestBody ChatMessageCreateRequest request,
        HttpServletResponse response
    ) throws IOException {
        long memberId = apiUser.userId();

        response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // 아래 두 헤더는 중간에 끼는 프록시가 응답을 모아두지 못하게 막는다.
        // 모아두면 조각이 한 번에 도착해 스트리밍이 사라진다. AI 서버도 같은 헤더를 보낸다.
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        response.setHeader("X-Accel-Buffering", "no");

        SseWriter writer = new SseWriter(response.getOutputStream(), objectMapper);
        try {
            chatService.answer(memberId, request.message(), writer);
        } catch (UncheckedIOException e) {
            // 학생이 화면을 닫으면 쓰기가 실패한다. 오류가 아니라 정상 종료다.
            log.debug("학생이 연결을 끊어 중계를 멈춥니다. memberId={}", memberId);
        }
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
