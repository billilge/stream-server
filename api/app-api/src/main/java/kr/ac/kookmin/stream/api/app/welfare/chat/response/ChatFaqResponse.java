package kr.ac.kookmin.stream.api.app.welfare.chat.response;

import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatFaq;

public record ChatFaqResponse(Long faqId, String question) {

    public static ChatFaqResponse from(ChatFaq faq) {
        return new ChatFaqResponse(faq.getId(), faq.getQuestion());
    }
}
