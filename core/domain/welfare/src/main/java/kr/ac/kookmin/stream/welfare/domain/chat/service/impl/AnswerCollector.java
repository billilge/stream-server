package kr.ac.kookmin.stream.welfare.domain.chat.service.impl;

import java.util.Optional;
import kr.ac.kookmin.stream.welfare.domain.chat.client.ChatEventListener;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * 조각을 그대로 흘려보내면서 답변 전체를 모은다. 흘려보내기만 하면 저장할 답변이 남지 않고, 모으기만 하면 스트리밍이 사라진다.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class AnswerCollector implements ChatEventListener {

    private final ChatEventListener delegate;
    private final StringBuilder collected = new StringBuilder();

    /** {@code done}을 받았는지. {@code error}로 끝났으면 false로 남는다. */
    private boolean completed;

    static AnswerCollector from(ChatEventListener delegate) {
        return new AnswerCollector(delegate);
    }

    @Override
    public void onDelta(String text) {
        collected.append(text);
        delegate.onDelta(text);
    }

    @Override
    public void onBubble() {
        // 말풍선 경계는 빈 줄로 잇는다. 프론트가 보여주는 모양과 같아진다
        collected.append("\n\n");
        delegate.onBubble();
    }

    @Override
    public void onDone() {
        completed = true;
        delegate.onDone();
    }

    @Override
    public void onError(String message) {
        delegate.onError(message);
    }

    /** {@code done}으로 끝났고 비어 있지 않을 때만 답변을 준다. */
    Optional<String> completedAnswer() {
        String answer = collected.toString().strip();
        if (!completed || answer.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(answer);
    }
}
