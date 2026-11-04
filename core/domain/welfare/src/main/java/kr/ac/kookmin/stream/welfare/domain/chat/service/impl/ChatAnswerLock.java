package kr.ac.kookmin.stream.welfare.domain.chat.service.impl;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * 한 회원당 답변 하나만 진행하게 막는다. 둘이 동시에 돌면 같은 이력을 읽어 가고 저장 순서가 꼬여 다음 질문의 이력이 뒤섞인다.
 * <p>
 * 서버 메모리에 두므로 백엔드가 한 대일 때만 정확하다. 여러 대가 되면 포트로 올리고 Redis 같은 공용 저장소 구현을 둔다.
 */
@Component
class ChatAnswerLock {

    private final Set<Long> answeringMembers = ConcurrentHashMap.newKeySet();

    /** 이미 답변 중이면 false. */
    boolean tryAcquire(Long memberId) {
        return answeringMembers.add(memberId);
    }

    void release(Long memberId) {
        answeringMembers.remove(memberId);
    }
}
