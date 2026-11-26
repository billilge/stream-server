package kr.ac.kookmin.stream.api.admin.welfare.chat;

import jakarta.validation.Valid;
import java.util.List;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.welfare.chat.request.AdminChatFaqWriteRequest;
import kr.ac.kookmin.stream.api.admin.welfare.chat.response.AdminChatFaqResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.welfare.domain.chat.service.AdminChatFaqService;
import kr.ac.kookmin.stream.welfare.domain.chat.service.ChatFaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/chat/faqs")
@RequiredArgsConstructor
public class AdminChatFaqController implements AdminChatFaqApi {

    private final AdminChatFaqService adminChatFaqService;
    private final ChatFaqService chatFaqService;

    @Override
    @GetMapping
    public ApiResponse<List<AdminChatFaqResponse>> getFaqs(AdminApiUser apiUser) {
        return ApiResponse.success(chatFaqService.getFaqs().stream().map(AdminChatFaqResponse::from).toList());
    }

    @Override
    @PostMapping
    public ApiResponse<Void> createFaq(AdminApiUser apiUser, @Valid @RequestBody AdminChatFaqWriteRequest request) {
        adminChatFaqService.create(request.question());
        return ApiResponse.success();
    }

    @Override
    @PutMapping("/{faqId}")
    public ApiResponse<Void> updateFaq(
        AdminApiUser apiUser,
        @PathVariable Long faqId,
        @Valid @RequestBody AdminChatFaqWriteRequest request
    ) {
        adminChatFaqService.updateQuestion(faqId, request.question());
        return ApiResponse.success();
    }

    @Override
    @DeleteMapping("/{faqId}")
    public ApiResponse<Void> deleteFaq(AdminApiUser apiUser, @PathVariable Long faqId) {
        adminChatFaqService.delete(faqId);
        return ApiResponse.success();
    }
}
