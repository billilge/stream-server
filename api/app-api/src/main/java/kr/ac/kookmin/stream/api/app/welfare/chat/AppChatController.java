package kr.ac.kookmin.stream.api.app.welfare.chat;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.UncheckedIOException;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.welfare.chat.request.ChatMessageCreateRequest;
import kr.ac.kookmin.stream.welfare.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final ObjectMapper objectMapper;

    @Override
    @PostMapping(value = "/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public void sendMessage(
        AppApiUser apiUser,
        @Valid @RequestBody ChatMessageCreateRequest request,
        HttpServletResponse response
    ) throws IOException {
        ChatSseWriter writer = ChatSseWriter.of(response, objectMapper);
        try {
            chatService.answer(apiUser.userId(), request.message(), writer);
        } catch (UncheckedIOException e) {
            // 학생이 화면을 닫으면 쓰기가 실패한다. 오류가 아니라 정상 종료다
            log.debug("학생이 연결을 끊어 중계를 멈춥니다. memberId={}", apiUser.userId());
        }
    }
}
