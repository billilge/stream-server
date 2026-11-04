package kr.ac.kookmin.stream.welfare.domain.chat.service;

import kr.ac.kookmin.stream.welfare.domain.chat.client.ChatEventListener;

public interface ChatService {

    /**
     * 질문을 AI 서버에 보내고 답변을 조각으로 흘려보낸다. 스트림이 끝날 때까지 돌아오지 않는다.
     * 질문과 답변은 {@code chat_messages}에 남긴다. 이력은 같은 대화({@code conversationId})에서만 붙인다.
     *
     * @param conversationId 채팅방에 들어올 때 프론트가 만든 대화 id
     * @param listener 받은 이벤트를 넘길 곳. 보통 컨트롤러가 학생 쪽 연결에 그대로 쓴다
     */
    void answer(Long memberId, String conversationId, String question, ChatEventListener listener);
}
