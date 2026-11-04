package kr.ac.kookmin.stream.welfare.domain.chat.client;

/**
 * AI 서버가 보내는 답변 이벤트를 받는 쪽. 답변을 모아서 한 번에 넘기지 않고 조각마다 알려, 받는 즉시 학생에게 흘려보낼 수 있게 한다.
 * <p>
 * 마지막에는 {@code onDone} 또는 {@code onError} 중 하나가 정확히 한 번 온다. {@link ChatClient} 구현체가 보장한다.
 * <p>
 * 더 받을 수 없으면(학생이 연결을 끊는 등) 구현체는 unchecked 예외를 던져 중계를 멈출 수 있다.
 * {@link ChatClient} 구현체는 이 예외를 AI 서버 실패로 다루지 않고 그대로 전파한다.
 */
public interface ChatEventListener {

    /** 답변 조각. 여러 번 온다. 빈 줄은 들어 있지 않다. */
    void onDelta(String text);

    /** 여기서부터 새 말풍선. 첫 말풍선 앞과 마지막 말풍선 뒤에는 오지 않는다. */
    void onBubble();

    /** 정상 종료. */
    void onDone();

    /**
     * 실패. 스트림이 시작된 뒤에는 상태 코드를 바꿀 수 없어 예외 대신 이 통로로 알린다.
     * 상세 원인은 로그에만 남기므로 여기 오는 글은 학생에게 보여줄 안내문이다.
     */
    void onError(String message);
}
