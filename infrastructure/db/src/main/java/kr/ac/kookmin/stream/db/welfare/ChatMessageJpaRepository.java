package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageJpaRepository extends JpaRepository<ChatMessageJpaEntity, Long> {

    /**
     * {@code message_id}로 정렬하는 이유는 {@code idx_chat_messages_member_id}를 그대로 쓰기 위해서다.
     * InnoDB가 세컨더리 인덱스 끝에 PK를 붙이므로 (member_id, message_id) 정렬이 인덱스로 커버된다.
     * {@code created_at}으로 정렬하면 인덱스에 없어 filesort가 생긴다.
     * <p>
     * {@code created_at}은 DB 기본값으로만 채워지고(insertable = false) 같은 초에 여러 건이 들어올 수 있어
     * 정렬 기준으로도 PK가 더 안전하다.
     */
    List<ChatMessageJpaEntity> findByMemberIdOrderByIdDesc(Long memberId, Pageable pageable);
}
