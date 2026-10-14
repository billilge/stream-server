package kr.ac.kookmin.stream.welfare.domain.chat.client;

import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatTurn;

/**
 * 답변을 만들어 주는 AI 서버. 구현체는 {@code infrastructure:client}에 둔다.
 */
public interface ChatClient {

    /**
     * 대화 이력을 보내고 답변을 조각으로 받는다. 스트림이 끝날 때까지 돌아오지 않는다.
     *
     * @param memberId 질문한 학생. AI 서버가 요청자를 식별하는 헤더로 쓴다
     * @param history 이번 질문까지 포함한 대화 전체. AI 서버는 대화를 저장하지 않으므로 요청마다 통째로 보낸다
     * @param listener 받은 이벤트를 넘길 곳. 계약은 {@link ChatEventListener} 참고
     */
    void stream(Long memberId, List<ChatTurn> history, ChatEventListener listener);
}
