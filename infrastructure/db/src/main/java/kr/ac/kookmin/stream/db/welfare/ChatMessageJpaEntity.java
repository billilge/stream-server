package kr.ac.kookmin.stream.db.welfare;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import kr.ac.kookmin.stream.db.common.BaseCreatedTimeEntity;
import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "chat_messages",
    indexes = {
        @Index(name = "idx_chat_messages_member_id", columnList = "member_id")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageJpaEntity extends BaseCreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "sender_type", nullable = false, length = 10)
    private String senderType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private ChatMessageJpaEntity(ChatMessage message) {
        this.id = message.getId();
        this.memberId = message.getMemberId();
        this.senderType = message.getSenderType();
        this.content = message.getContent();
    }

    public static ChatMessageJpaEntity from(ChatMessage message) {
        return new ChatMessageJpaEntity(message);
    }

    public ChatMessage toDomain() {
        return ChatMessage.of(id, memberId, senderType, content);
    }
}
