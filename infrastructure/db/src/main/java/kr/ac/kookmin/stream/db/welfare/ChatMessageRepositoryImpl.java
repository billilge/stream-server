package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatMessage;
import kr.ac.kookmin.stream.welfare.domain.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepositoryImpl implements ChatMessageRepository {

    private final ChatMessageJpaRepository chatMessageJpaRepository;

    @Override
    public List<ChatMessage> findRecentByMemberId(long memberId, int limit) {
        return chatMessageJpaRepository
            .findByMemberIdOrderByIdDesc(memberId, PageRequest.of(0, limit))
            .stream()
            .map(ChatMessageJpaEntity::toDomain)
            .toList();
    }

    @Override
    public ChatMessage save(ChatMessage message) {
        return chatMessageJpaRepository.save(ChatMessageJpaEntity.from(message)).toDomain();
    }
}
