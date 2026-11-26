package kr.ac.kookmin.stream.api.admin.welfare.chat.response;

import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatFaq;

public record AdminChatFaqResponse(Long faqId, String question) {

    public static AdminChatFaqResponse from(ChatFaq faq) {
        return new AdminChatFaqResponse(faq.getId(), faq.getQuestion());
    }
}
