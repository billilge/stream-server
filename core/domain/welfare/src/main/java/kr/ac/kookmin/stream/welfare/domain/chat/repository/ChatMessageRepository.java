package kr.ac.kookmin.stream.welfare.domain.chat.repository;

import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatMessage;

public interface ChatMessageRepository {

    /**
     * 한 학생의 최근 대화를 <b>최신순</b>으로 가져온다.
     * <p>
     * AI 서버는 대화를 저장하지 않으므로 요청마다 이력을 통째로 보내야 한다. 전부 보내면
     * 학기가 갈수록 토큰 비용이 계속 늘어나므로 최근 것만 자른다.
     * <p>
     * 돌려주는 순서가 최신순인 이유는 "최근 N개"를 자르려면 그 방향으로 정렬해야 하기 때문이다.
     * AI 서버에 보낼 때는 오래된 것부터 와야 하므로 쓰는 쪽에서 뒤집는다.
     */
    List<ChatMessage> findRecentByMemberId(long memberId, int limit);

    ChatMessage save(ChatMessage message);
}
