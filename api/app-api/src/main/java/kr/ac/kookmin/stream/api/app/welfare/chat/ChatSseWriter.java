package kr.ac.kookmin.stream.api.app.welfare.chat;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import kr.ac.kookmin.stream.api.app.welfare.chat.response.ChatDeltaResponse;
import kr.ac.kookmin.stream.api.app.welfare.chat.response.ChatErrorResponse;
import kr.ac.kookmin.stream.welfare.domain.chat.client.ChatEventListener;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;

/**
 * 받은 이벤트를 학생 쪽 연결에 SSE 형식({@code event: 이름}, {@code data: JSON}, 빈 줄)으로 쓰고 조각마다 바로 flush한다.
 * <p>
 * 응답 스트림에 <b>직접</b> 쓴다. {@code StreamingResponseBody}로는 flush가 무시되어 톰캣 버퍼가 찰 때만 조각이 나갔다.
 * 대신 답변이 끝날 때까지 요청 스레드를 쥐므로, 동시 대화가 톰캣 최대 스레드(기본 200)에 가까워지면 {@code SseEmitter}를 다시 시험한다.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class ChatSseWriter implements ChatEventListener {

    // bubble·done은 data가 빈 객체다. 필드 없는 record는 직렬화 설정에 따라 예외가 날 수 있어 그대로 쓴다
    private static final String EMPTY_DATA = "{}";

    private final OutputStream output;
    private final ObjectMapper objectMapper;

    /** 응답 헤더를 SSE로 정하고 출력 스트림을 연다. */
    static ChatSseWriter of(HttpServletResponse response, ObjectMapper objectMapper) throws IOException {
        response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // 중간 프록시가 응답을 모아두면 조각이 한 번에 도착해 스트리밍이 사라진다. AI 서버도 같은 헤더를 보낸다
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        response.setHeader("X-Accel-Buffering", "no");
        return new ChatSseWriter(response.getOutputStream(), objectMapper);
    }

    @Override
    public void onDelta(String text) {
        write("delta", objectMapper.writeValueAsString(new ChatDeltaResponse(text)));
    }

    @Override
    public void onBubble() {
        write("bubble", EMPTY_DATA);
    }

    @Override
    public void onDone() {
        write("done", EMPTY_DATA);
    }

    @Override
    public void onError(String message) {
        write("error", objectMapper.writeValueAsString(new ChatErrorResponse(message)));
    }

    private void write(String event, String data) {
        try {
            String frame = "event: " + event + "\ndata: " + data + "\n\n";
            output.write(frame.getBytes(StandardCharsets.UTF_8));
            output.flush();
        } catch (IOException e) {
            // 학생이 연결을 끊었다. 받을 사람이 없으니 서비스까지 올려 보내 중계를 멈춘다
            throw new UncheckedIOException(e);
        }
    }
}
