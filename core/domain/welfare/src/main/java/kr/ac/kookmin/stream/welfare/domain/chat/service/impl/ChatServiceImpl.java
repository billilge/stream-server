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

    // 질문·답변 5쌍. 전부 보내면 학기가 갈수록 토큰 비용이 늘고, 이 정도면 후속 질문은 이어진다
    private static final int HISTORY_LIMIT = 10;

    private static final String BUSY_MESSAGE = "이전 답변이 아직 끝나지 않았습니다. 답변이 끝난 뒤에 다시 질문해 주세요.";

    private final ChatMessageRepository chatMessageRepository;
    private final ChatClient chatClient;
    private final ChatAnswerLock chatAnswerLock;

    /**
     * {@code @Transactional}을 붙이지 않는다. 답변 생성이 10초쯤 걸려 그동안 DB 커넥션을 쥐면 챗봇과 무관한 화면까지 멈춘다.
     * 저장은 각자 짧게 하고 스트리밍은 트랜잭션 밖에서 한다. 그래서 답변이 실패하면 질문만 남는다.
     * <p>
     * 이미 답변 중인 회원의 질문은 {@code error} 이벤트로 거절하고 저장하지 않는다. 컨트롤러가 SSE 헤더를 먼저 정해 두어
     * 상태 코드로는 거절할 수 없다.
     */
    @Override
    public void answer(long memberId, String question, ChatEventListener listener) {
        if (!chatAnswerLock.tryAcquire(memberId)) {
            listener.onError(BUSY_MESSAGE);
            return;
        }
        // 학생이 연결을 끊으면 예외가 올라오므로 반드시 finally에서 푼다. 안 풀면 재시작 전까지 질문할 수 없다
        try {
            answerInTurn(memberId, question, listener);
        } finally {
            chatAnswerLock.release(memberId);
        }
    }

    private void answerInTurn(long memberId, String question, ChatEventListener listener) {
        List<ChatTurn> turns = loadRecentTurns(memberId);
        turns.add(ChatTurn.user(question));
        chatMessageRepository.save(ChatMessage.of(null, memberId, ChatTurn.USER, question));

        AnswerCollector collector = AnswerCollector.from(listener);
        chatClient.stream(memberId, turns, collector);

        // 중간에 끊긴 답변이나 빈 답변은 다음 질문의 이력에 섞이므로 남기지 않는다. 실패 원인은 클라이언트가 로그로 남긴다
        collector.completedAnswer()
            .ifPresent(answer -> chatMessageRepository.save(ChatMessage.of(null, memberId, ChatTurn.MODEL, answer)));
    }

    /** 최근 대화를 오래된 것부터 돌려준다. 저장소는 최신순으로 준다. */
    private List<ChatTurn> loadRecentTurns(long memberId) {
        List<ChatMessage> recent = chatMessageRepository.findRecentByMemberId(memberId, HISTORY_LIMIT);

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
}
