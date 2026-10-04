package kr.ac.kookmin.stream.welfare.domain.chat.service.impl;

import java.util.ArrayList;
import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.chat.client.ChatClient;
import kr.ac.kookmin.stream.welfare.domain.chat.client.ChatEventListener;
import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatMessage;
import kr.ac.kookmin.stream.welfare.domain.chat.domain.ChatTurn;
import kr.ac.kookmin.stream.welfare.domain.chat.repository.ChatMessageRepository;
import kr.ac.kookmin.stream.welfare.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class ChatServiceImpl implements ChatService {

    /**
     * AI 서버에 함께 보낼 지난 대화 건수. 질문·답변 한 쌍이 2건이므로 5번의 주고받음에 해당한다.
     * <p>
     * 전부 보내면 학기가 갈수록 토큰 비용이 계속 늘어난다. 이 정도면 "그럼 언제까지야?" 같은
     * 후속 질문은 이어진다.
     * <p>
     * 설정값으로 빼지 않은 이유는 이 저장소가 core 도메인 모듈에 설정 바인딩을 두지 않기 때문이다
     * ({@code @ConfigurationProperties}는 gateway와 infrastructure에만 있다).
     * 운영 중에 조절할 필요가 생기면 {@code StreamAiProperties}로 옮기고 클라이언트가 자르게 한다.
     */
    private static final int HISTORY_LIMIT = 10;

    private final ChatMessageRepository chatMessageRepository;
    private final ChatClient chatClient;

    /**
     * 이 메서드에 {@code @Transactional}을 붙이지 않는다.
     * <p>
     * 답변 생성이 10초쯤 걸리는데 트랜잭션으로 감싸면 그 시간 동안 DB 커넥션을 쥐고 있게 된다.
     * 동시에 질문하는 학생이 몇 명만 되어도 커넥션 풀이 말라 <b>챗봇과 무관한 화면까지 멈춘다.</b>
     * 그래서 저장은 각자 짧게 하고, 긴 스트리밍은 트랜잭션 밖에서 한다.
     * <p>
     * 그 대가로 질문만 남고 답변이 안 남는 경우가 생길 수 있다. 답변 생성이 실패한 경우이므로
     * 기록으로는 그게 맞다 — 무엇을 물었는지는 남아야 나중에 오답을 찾을 수 있다.
     */
    @Override
    public void answer(long memberId, String question, ChatEventListener listener) {
        List<ChatTurn> turns = loadRecentTurns(memberId);
        turns.add(ChatTurn.user(question));

        chatMessageRepository.save(ChatMessage.of(null, memberId, ChatTurn.USER, question));

        AnswerCollector collector = new AnswerCollector(listener);
        chatClient.stream(memberId, turns, collector);

        String answer = collector.answer();
        // 실패했거나 답변이 비었다. 빈 답변을 남기면 다음 질문의 이력에 빈 턴이 섞인다.
        // 실패 원인은 클라이언트가 이미 로그에 남겼다. core 도메인 모듈에는 로거가 없다.
        if (answer.isBlank()) {
            return;
        }
        chatMessageRepository.save(ChatMessage.of(null, memberId, ChatTurn.MODEL, answer));
    }

    /** 최근 대화를 오래된 것부터 순서대로 돌려준다. */
    private List<ChatTurn> loadRecentTurns(long memberId) {
        List<ChatMessage> recent = chatMessageRepository.findRecentByMemberId(memberId, HISTORY_LIMIT);

        // 저장소는 최신순으로 준다. AI 서버는 대화 순서대로 받아야 하므로 뒤집는다.
        List<ChatTurn> turns = new ArrayList<>(recent.size() + 1);
        for (int i = recent.size() - 1; i >= 0; i--) {
            ChatMessage message = recent.get(i);
            if (message.getContent() == null || message.getContent().isBlank()) {
                continue;
            }
            turns.add(new ChatTurn(message.getSenderType(), message.getContent()));
        }
        return turns;
    }

    /**
     * 조각을 그대로 흘려보내면서 동시에 답변 전체를 모은다.
     * <p>
     * 둘을 같이 하는 이유는, 통과만 시키면 저장할 답변이 남지 않고 모으기만 하면 스트리밍이 사라지기 때문이다.
     */
    private static final class AnswerCollector implements ChatEventListener {

        private final ChatEventListener delegate;
        private final StringBuilder collected = new StringBuilder();

        private AnswerCollector(ChatEventListener delegate) {
            this.delegate = delegate;
        }

        @Override
        public void onDelta(String text) {
            collected.append(text);
            delegate.onDelta(text);
        }

        @Override
        public void onBubble() {
            // 말풍선 경계는 빈 줄로 이어 붙인다. 프론트가 화면에 보여주는 모양과 같아진다.
            collected.append("\n\n");
            delegate.onBubble();
        }

        @Override
        public void onDone() {
            delegate.onDone();
        }

        @Override
        public void onError(String message) {
            delegate.onError(message);
        }

        String answer() {
            return collected.toString().strip();
        }
    }
}
