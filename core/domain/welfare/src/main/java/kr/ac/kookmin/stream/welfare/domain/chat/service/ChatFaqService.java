package kr.ac.kookmin.stream.welfare.domain.chat.service;

import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatFaq;

public interface ChatFaqService {

    /** 등록 순서대로. */
    List<ChatFaq> getFaqs();
}
