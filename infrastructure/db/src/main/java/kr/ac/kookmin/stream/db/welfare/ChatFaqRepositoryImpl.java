package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.db.common.ConstraintViolationUtil;
import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatErrorCode;
import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatFaq;
import kr.ac.kookmin.stream.welfare.domain.chat.repository.ChatFaqRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatFaqRepositoryImpl implements ChatFaqRepository {

    private static final String ACTIVE_SLOT_UNIQUE_CONSTRAINT = "uk_chat_faqs_active_slot";

    private final ChatFaqJpaRepository chatFaqJpaRepository;

    @Override
    public List<ChatFaq> findAll() {
        return chatFaqJpaRepository.findAllByIsDeletedFalseOrderByIdAsc().stream()
            .map(ChatFaqJpaEntity::toDomain)
            .toList();
    }

    @Override
    public Optional<ChatFaq> findById(Long id) {
        return chatFaqJpaRepository.findByIdAndIsDeletedFalse(id).map(ChatFaqJpaEntity::toDomain);
    }

    @Override
    public ChatFaq save(ChatFaq faq) {
        try {
            // 유니크 위반을 이 자리에서 잡으려면 바로 반영해야 한다
            return chatFaqJpaRepository.saveAndFlush(ChatFaqJpaEntity.from(faq)).toDomain();
        } catch (DataIntegrityViolationException e) {
            if (ConstraintViolationUtil.isViolated(e, ACTIVE_SLOT_UNIQUE_CONSTRAINT)) {
                throw new BusinessException(ChatErrorCode.CHAT_FAQ_ADD_CONFLICT);
            }
            throw e;
        }
    }

    @Override
    public boolean delete(Long id) {
        return chatFaqJpaRepository.findByIdAndIsDeletedFalse(id)
            .map(entity -> {
                entity.delete();
                return true;
            })
            .orElse(false);
    }
}
