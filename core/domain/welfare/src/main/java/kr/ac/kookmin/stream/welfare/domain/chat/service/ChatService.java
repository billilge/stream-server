package kr.ac.kookmin.stream.welfare.domain.chat.service;

import kr.ac.kookmin.stream.welfare.domain.chat.client.ChatEventListener;

public interface ChatService {

    /**
     * 질문을 AI 서버에 보내고 답변을 조각으로 흘려보낸다. 스트림이 끝날 때까지 돌아오지 않는다.
     * <p>
     * 질문과 답변을 {@code chat_messages}에 남긴다. 9/21 회의에서 문의기록을 화면에서 빼고
     * DB에만 남기기로 한 것이 이 저장이다.
     *
     * @param listener 받은 이벤트를 넘길 곳. 보통 컨트롤러가 학생 쪽 연결에 그대로 쓴다
     */
    void answer(long memberId, String question, ChatEventListener listener);
}
