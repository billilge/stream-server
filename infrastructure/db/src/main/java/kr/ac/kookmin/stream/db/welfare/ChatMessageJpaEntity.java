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
        @Index(name = "idx_chat_messages_member_id_conversation_id", columnList = "member_id, conversation_id")
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

    @Column(name = "conversation_id", nullable = false, length = 36, columnDefinition = "CHAR(36)")
    private String conversationId;

    @Column(name = "sender_type", nullable = false, length = 10)
    private String senderType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private ChatMessageJpaEntity(ChatMessage message) {
        this.id = message.getId();
        this.memberId = message.getMemberId();
        this.conversationId = message.getConversationId();
        this.senderType = message.getSenderType();
        this.content = message.getContent();
    }

    public static ChatMessageJpaEntity from(ChatMessage message) {
        return new ChatMessageJpaEntity(message);
    }

    public ChatMessage toDomain() {
        return ChatMessage.of(id, memberId, conversationId, senderType, content);
    }
}
