package kr.ac.kookmin.stream.welfare.domain.chat.client;

/**
 * AI 서버가 보내는 SSE 이벤트를 받는 쪽. 이벤트 네 종류를 그대로 메서드로 둔다.
 * <p>
 * 답변 전체를 모아서 한 번에 넘기지 않고 조각마다 알리는 이유는, 중계하는 쪽이 받는 즉시
 * 학생에게 흘려보내야 하기 때문이다. 모아두면 학생이 10초 동안 빈 화면을 본다.
 * <p>
 * 구현하는 쪽은 조각을 흘려보내면서 동시에 모아야 한다. 모으지 않으면
 * {@code chat_messages}에 저장할 답변 전체가 남지 않는다.
 */
public interface ChatEventListener {

    /** 답변 조각. 여러 번 온다. 빈 줄은 들어 있지 않다. */
    void onDelta(String text);

    /** 여기서부터 새 말풍선. 첫 말풍선 앞과 마지막 말풍선 뒤에는 오지 않는다. */
    void onBubble();

    /** 정상 종료. */
    void onDone();

    /**
     * 실패. 상세 원인은 AI 서버 로그에만 남으므로 여기 오는 글은 학생에게 보여줄 안내문이다.
     * <p>
     * 스트림이 시작된 뒤의 실패는 상태 코드를 바꿀 수 없어 예외로 올릴 수 없다.
     * 그래서 성공 경로와 같은 통로로 알린다.
     */
    void onError(String message);
}
