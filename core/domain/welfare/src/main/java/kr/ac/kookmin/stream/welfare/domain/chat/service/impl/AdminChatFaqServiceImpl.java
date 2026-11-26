package kr.ac.kookmin.stream.welfare.domain.chat.service.impl;

import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatErrorCode;
import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatFaq;
import kr.ac.kookmin.stream.welfare.domain.chat.repository.ChatFaqRepository;
import kr.ac.kookmin.stream.welfare.domain.chat.service.AdminChatFaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class AdminChatFaqServiceImpl implements AdminChatFaqService {

    private final ChatFaqRepository chatFaqRepository;

    @Override
    public List<ChatFaq> getFaqs() {
        return chatFaqRepository.findAll();
    }

    @Override
    @Transactional
    public void create(String question) {
        chatFaqRepository.save(ChatFaq.create(question, chatFaqRepository.findUsedSlots()));
    }

    @Override
    @Transactional
    public void updateQuestion(Long id, String question) {
        ChatFaq faq = chatFaqRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ChatErrorCode.CHAT_FAQ_NOT_FOUND));
        faq.changeQuestion(question);
        chatFaqRepository.save(faq);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!chatFaqRepository.delete(id)) {
            throw new BusinessException(ChatErrorCode.CHAT_FAQ_NOT_FOUND);
        }
    }
}
