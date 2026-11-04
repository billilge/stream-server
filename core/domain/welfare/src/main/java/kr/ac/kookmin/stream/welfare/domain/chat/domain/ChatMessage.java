package kr.ac.kookmin.stream.welfare.domain.chat.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessage {

    private Long id;
    private Long memberId;
    private String conversationId;
    private String senderType;
    private String content;

    public static ChatMessage of(Long id, Long memberId, String conversationId, String senderType, String content) {
        return new ChatMessage(id, memberId, conversationId, senderType, content);
    }

    public static ChatMessage create(Long memberId, String conversationId, String senderType, String content) {
        return new ChatMessage(null, memberId, conversationId, senderType, content);
    }
}
