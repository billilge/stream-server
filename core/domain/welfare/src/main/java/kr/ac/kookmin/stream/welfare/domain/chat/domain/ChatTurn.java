package kr.ac.kookmin.stream.welfare.domain.chat.domain;

/**
 * AI 서버에 보내는 대화 한 턴. 역할과 글만 담는다.
 * <p>
 * {@link ChatMessage}를 그대로 보내지 않는 이유는 그쪽에 id와 memberId가 들어 있어서다.
 * AI 서버는 누가 물었는지 알 필요가 없고, 저장용 식별자를 외부로 내보낼 이유도 없다.
 * <p>
 * {@code role} 값은 AI 서버 계약과 {@code chat_messages.sender_type} 양쪽에서 같은 글자를 쓴다.
 * 중간에 변환하면 한쪽만 고쳤을 때 조용히 어긋난다.
 */
public record ChatTurn(String role, String text) {

    /** 학생이 쓴 글. */
    public static final String USER = "user";

    /** 챗봇이 쓴 글. Gemini 계약이 assistant가 아니라 model이다. */
    public static final String MODEL = "model";

    public ChatTurn {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("role이 비어 있습니다.");
        }
        // AI 서버가 빈 글에 422를 돌려준다. 보내기 전에 막아 원인을 여기서 알 수 있게 한다.
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("text가 비어 있습니다.");
        }
    }

    public static ChatTurn user(String text) {
        return new ChatTurn(USER, text);
    }

    public static ChatTurn model(String text) {
        return new ChatTurn(MODEL, text);
    }
}
