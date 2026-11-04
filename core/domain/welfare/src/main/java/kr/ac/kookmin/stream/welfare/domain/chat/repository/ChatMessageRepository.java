package kr.ac.kookmin.stream.welfare.domain.chat.repository;

import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatMessage;

public interface ChatMessageRepository {

    /**
     * 한 학생의 한 대화에서 최근 메시지를 <b>최신순</b>으로 가져온다. "최근 N개"를 자르려면 이 방향으로 정렬해야 하므로,
     * 오래된 것부터 필요한 쪽(AI 서버에 보낼 때)은 쓰는 쪽에서 뒤집는다.
     * <p>
     * {@code memberId}를 같이 받는 이유는 다른 학생의 {@code conversationId}를 보내도 그 대화가 붙지 않게 하기 위해서다.
     */
    List<ChatMessage> findRecentByConversation(Long memberId, String conversationId, int limit);

    ChatMessage save(ChatMessage message);
}
