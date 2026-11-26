package kr.ac.kookmin.stream.welfare.domain.chat.service.impl;

import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatFaq;
import kr.ac.kookmin.stream.welfare.domain.chat.repository.ChatFaqRepository;
import kr.ac.kookmin.stream.welfare.domain.chat.service.ChatFaqService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class ChatFaqServiceImpl implements ChatFaqService {

    private final ChatFaqRepository chatFaqRepository;

    @Override
    public List<ChatFaq> getFaqs() {
        return chatFaqRepository.findAll();
    }
}
