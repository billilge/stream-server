package kr.ac.kookmin.stream.client.chat.streamai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * SSE 본문을 줄 단위로 읽어 이벤트마다 넘긴다. 형식은 {@code event: 이름}, {@code data: JSON}, 빈 줄 한 번이다.
 * <p>
 * {@code data}의 JSON은 줄바꿈이 {@code \\n}으로 escape되어 와서 한 줄을 넘지 않으므로 줄 단위로 읽어도 쪼개지지 않는다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class SseEventReader {

    private static final String EVENT_PREFIX = "event:";
    private static final String DATA_PREFIX = "data:";

    // 다 읽기 전에 멈추면(handler가 예외를 던지면) 여기서 닫아야 AI 서버 스트림이 바로 끊긴다.
    // RestClient가 응답을 닫을 때는 남은 본문을 끝까지 읽어 버려 답변이 다 만들어질 때까지 기다리게 된다
    static void read(InputStream body, Consumer<SseEvent> handler) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(body, StandardCharsets.UTF_8))) {
            String name = null;
            String data = null;

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    emit(name, data, handler);
                    name = null;
                    data = null;
                    continue;
                }
                if (line.startsWith(EVENT_PREFIX)) {
                    name = line.substring(EVENT_PREFIX.length()).trim();
                } else if (line.startsWith(DATA_PREFIX)) {
                    data = line.substring(DATA_PREFIX.length()).trim();
                }
                // 그 외 줄(주석 등)은 무시한다. 계약에 없지만 와도 깨지지 않아야 한다
            }

            // 마지막 이벤트 뒤에 빈 줄 없이 끊긴 경우
            emit(name, data, handler);
        }
    }

    private static void emit(String name, String data, Consumer<SseEvent> handler) {
        if (name != null) {
            handler.accept(new SseEvent(name, data));
        }
    }
}
