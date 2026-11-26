package kr.ac.kookmin.stream.db.welfare;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.ac.kookmin.stream.db.common.BaseSoftDeleteEntity;
import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatFaq;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
// uk_chat_faqs_active_slot(V68)는 generated column active_slot에 걸려 있다.
// Entity에 매핑된 컬럼이 아니라 여기서는 선언하지 않는다.
// 도메인이 isDeleted를 들고 있지 않아 from(faq)은 늘 is_deleted = false다. 바뀐 컬럼만 UPDATE해야 질문 수정이
// 동시 삭제를 되돌리지 않는다
@DynamicUpdate
@Table(name = "chat_faqs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatFaqJpaEntity extends BaseSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_faq_id")
    private Long id;

    @Column(nullable = false, length = 100)
    private String question;

    @Column(nullable = false)
    private int slot;

    private ChatFaqJpaEntity(ChatFaq faq) {
        this.id = faq.getId();
        this.question = faq.getQuestion();
        this.slot = faq.getSlot();
    }

    public static ChatFaqJpaEntity from(ChatFaq faq) {
        return new ChatFaqJpaEntity(faq);
    }

    public ChatFaq toDomain() {
        return ChatFaq.of(id, question, slot);
    }
}
