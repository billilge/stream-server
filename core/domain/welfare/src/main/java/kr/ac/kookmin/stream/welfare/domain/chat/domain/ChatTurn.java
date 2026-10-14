package kr.ac.kookmin.stream.welfare.domain.chat.domain;

/**
 * AI 서버에 보내는 대화 한 턴. {@link ChatMessage}의 id·memberId는 AI 서버가 알 필요가 없어 역할과 글만 담는다.
 * <p>
 * {@code role} 값은 AI 서버 계약과 {@code chat_messages.sender_type}에 같은 글자로 쓴다. 중간에 변환하면 한쪽만 고쳤을 때 조용히 어긋난다.
 */
public record ChatTurn(String role, String text) {

    /** 학생이 쓴 글. */
    public static final String USER = "user";

    /** 챗봇이 쓴 글. Gemini 계약이 assistant가 아니라 model이다. */
    public static final String MODEL = "model";

    public ChatTurn {
        validateRole(role);
        validateText(text);
    }

    public static ChatTurn from(ChatMessage message) {
        return new ChatTurn(message.getSenderType(), message.getContent());
    }

    public static ChatTurn user(String text) {
        return new ChatTurn(USER, text);
    }

    private static void validateRole(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("role이 비어 있습니다.");
        }
    }

    // AI 서버가 빈 글에 422를 돌려준다. 보내기 전에 막아 원인을 여기서 알 수 있게 한다
    private static void validateText(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("text가 비어 있습니다.");
        }
    }
}
