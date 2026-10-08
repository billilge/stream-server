package kr.ac.kookmin.stream.welfare.domain.chat.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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

    private static final String BUSY_MESSAGE = "이전 답변이 아직 끝나지 않았습니다. 답변이 끝난 뒤에 다시 질문해 주세요.";

    private final ChatMessageRepository chatMessageRepository;
    private final ChatClient chatClient;

    /**
     * 지금 답변을 받고 있는 회원. 한 사람당 답변 하나만 진행한다.
     * <p>
     * 둘이 동시에 돌면 두 요청이 같은 이력을 읽어 가고, 저장 순서가 꼬여 다음 질문의 이력이 뒤섞인다.
     * <p>
     * 서버 메모리에 두므로 <b>백엔드가 한 대일 때만</b> 정확하다. 여러 대로 늘리면 Redis 같은
     * 공용 저장소로 옮겨야 한다.
     */
    private final Set<Long> answeringMembers = ConcurrentHashMap.newKeySet();

    /**
     * 이 메서드에 {@code @Transactional}을 붙이지 않는다.
     * <p>
     * 답변 생성이 10초쯤 걸리는데 트랜잭션으로 감싸면 그 시간 동안 DB 커넥션을 쥐고 있게 된다.
     * 동시에 질문하는 학생이 몇 명만 되어도 커넥션 풀이 말라 <b>챗봇과 무관한 화면까지 멈춘다.</b>
     * 그래서 저장은 각자 짧게 하고, 긴 스트리밍은 트랜잭션 밖에서 한다.
     * <p>
     * 그 대가로 질문만 남고 답변이 안 남는 경우가 생길 수 있다. 답변 생성이 실패한 경우이므로
     * 기록으로는 그게 맞다 — 무엇을 물었는지는 남아야 나중에 오답을 찾을 수 있다.
     * <p>
     * 이미 답변 중인 회원이 또 질문하면 {@code error} 이벤트로 거절하고 질문도 저장하지 않는다.
     * 409 같은 상태 코드로 거절하지 않는 이유는, 컨트롤러가 이 메서드를 부르기 전에 이미
     * {@code text/event-stream} 헤더를 정해 두어 JSON 에러 응답을 쓸 수 없기 때문이다.
     */
    @Override
    public void answer(long memberId, String question, ChatEventListener listener) {
        if (!answeringMembers.add(memberId)) {
            listener.onError(BUSY_MESSAGE);
            return;
        }
        // 학생이 화면을 닫아 쓰기가 실패해도 예외가 올라오므로, 반드시 finally에서 풀어준다.
        // 안 풀면 그 학생은 서버를 재시작할 때까지 질문할 수 없다.
        try {
            answerInTurn(memberId, question, listener);
        } finally {
            answeringMembers.remove(memberId);
        }
    }

    private void answerInTurn(long memberId, String question, ChatEventListener listener) {
        List<ChatTurn> turns = loadRecentTurns(memberId);
        turns.add(ChatTurn.user(question));

        chatMessageRepository.save(ChatMessage.of(null, memberId, ChatTurn.USER, question));

        AnswerCollector collector = new AnswerCollector(listener);
        chatClient.stream(memberId, turns, collector);

        String answer = collector.answer();
        // 실패했거나 답변이 비었다. 둘 다 저장하지 않는다.
        // 몇 조각 받은 뒤 실패하면 텍스트는 있지만 중간에 끊긴 답변이다. 남기면 다음 질문의 이력에 섞인다.
        // 빈 답변을 남기면 다음 질문의 이력에 빈 턴이 섞인다.
        // 실패 원인은 클라이언트가 이미 로그에 남겼다. core 도메인 모듈에는 로거가 없다.
        if (!collector.isCompleted() || answer.isBlank()) {
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

        /** {@code done}을 받았는지. {@code error}로 끝났으면 false로 남는다. */
        private boolean completed;

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
            completed = true;
            delegate.onDone();
        }

        @Override
        public void onError(String message) {
            delegate.onError(message);
        }

        String answer() {
            return collected.toString().strip();
        }

        boolean isCompleted() {
            return completed;
        }
    }
}
