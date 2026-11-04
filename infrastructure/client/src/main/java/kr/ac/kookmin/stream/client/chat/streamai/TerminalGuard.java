package kr.ac.kookmin.stream.client.chat.streamai;

import kr.ac.kookmin.stream.welfare.domain.chat.client.ChatEventListener;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * {@code done}·{@code error} 중 하나만, 한 번만 넘기고 끝난 뒤의 이벤트는 버린다.
 * 학생 쪽 연결을 두 번 닫으려 하면 예외가 나고, 한 번도 닫지 않으면 연결이 계속 열려 있다.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
final class TerminalGuard implements ChatEventListener {

    private final ChatEventListener delegate;
    private boolean finished;

    static TerminalGuard from(ChatEventListener delegate) {
        return new TerminalGuard(delegate);
    }

    @Override
    public void onDelta(String text) {
        if (!finished) {
            delegate.onDelta(text);
        }
    }

    @Override
    public void onBubble() {
        if (!finished) {
            delegate.onBubble();
        }
    }

    @Override
    public void onDone() {
        if (finished) {
            return;
        }
        finished = true;
        delegate.onDone();
    }

    @Override
    public void onError(String message) {
        if (finished) {
            return;
        }
        finished = true;
        delegate.onError(message);
    }
}
