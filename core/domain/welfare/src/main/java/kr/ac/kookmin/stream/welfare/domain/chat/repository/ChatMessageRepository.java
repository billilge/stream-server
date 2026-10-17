package kr.ac.kookmin.stream.welfare.domain.chat.repository;

import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatMessage;

public interface ChatMessageRepository {

    /**
     * 한 학생의 최근 대화를 <b>최신순</b>으로 가져온다. "최근 N개"를 자르려면 이 방향으로 정렬해야 하므로,
     * 오래된 것부터 필요한 쪽(AI 서버에 보낼 때)은 쓰는 쪽에서 뒤집는다.
     */
    List<ChatMessage> findRecentByMemberId(Long memberId, int limit);

    ChatMessage save(ChatMessage message);
}
