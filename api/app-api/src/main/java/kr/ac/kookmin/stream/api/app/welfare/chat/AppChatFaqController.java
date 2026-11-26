package kr.ac.kookmin.stream.api.app.welfare.chat;

import java.util.List;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.welfare.chat.response.ChatFaqResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.welfare.domain.chat.service.ChatFaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/app/chat/faqs")
@RequiredArgsConstructor
public class AppChatFaqController implements AppChatFaqApi {

    private final ChatFaqService chatFaqService;

    @Override
    @GetMapping
    public ApiResponse<List<ChatFaqResponse>> getFaqs(AppApiUser apiUser) {
        return ApiResponse.success(chatFaqService.getFaqs().stream().map(ChatFaqResponse::from).toList());
    }
}
