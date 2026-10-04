package kr.ac.kookmin.stream.welfare.domain.chat.client;

import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatTurn;

/**
 * 답변을 만들어 주는 AI 서버. 구현체는 {@code infrastructure:client}에 둔다.
 * <p>
 * 인터페이스를 core에 두는 이유는 의존 방향 때문이다. 서비스가 구현체를 직접 알면
 * core가 infrastructure를 향하게 되어 Gradle 경계 검사에서 막힌다.
 */
public interface ChatClient {

    /**
     * 대화 이력을 보내고 답변을 조각으로 받는다. 이 메서드는 스트림이 끝날 때까지 돌아오지 않는다.
     * <p>
     * 구현체는 {@code listener}의 {@code onDone} 또는 {@code onError}를 <b>정확히 한 번</b> 부른다.
     * 둘 다 안 불리면 학생 쪽 연결이 끝나지 않고 계속 열려 있게 된다.
     *
     * @param memberId 질문한 학생. AI 서버가 요청자를 식별하는 헤더로 쓴다
     * @param history 이번 질문까지 포함한 대화 전체. 마지막 턴이 이번 질문이다.
     *                AI 서버는 대화를 저장하지 않으므로 요청마다 통째로 보내야 한다.
     * @param listener 받은 이벤트를 넘길 곳
     */
    void stream(long memberId, List<ChatTurn> history, ChatEventListener listener);
}
