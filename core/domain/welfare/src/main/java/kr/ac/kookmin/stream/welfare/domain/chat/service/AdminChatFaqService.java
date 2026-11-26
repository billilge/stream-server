package kr.ac.kookmin.stream.welfare.domain.chat.service;

import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatFaq;

public interface AdminChatFaqService {

    /** 등록 순서대로. */
    List<ChatFaq> getFaqs();

    void create(String question);

    void updateQuestion(Long id, String question);

    void delete(Long id);
}
