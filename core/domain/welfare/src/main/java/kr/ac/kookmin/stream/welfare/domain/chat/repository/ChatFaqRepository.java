package kr.ac.kookmin.stream.welfare.domain.chat.repository;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatFaq;

public interface ChatFaqRepository {

    /** 등록 순서대로. */
    List<ChatFaq> findAll();

    Optional<ChatFaq> findById(Long id);

    /** 동시 추가로 같은 자리를 잡았으면 {@code CHAT_FAQ_ADD_CONFLICT}를 던진다. */
    ChatFaq save(ChatFaq faq);

    boolean delete(Long id);
}
