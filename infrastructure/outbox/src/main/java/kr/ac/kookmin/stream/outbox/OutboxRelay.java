package kr.ac.kookmin.stream.outbox;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.common.DateUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import tools.jackson.databind.json.JsonMapper;

/**
 * 아웃박스에 쌓인 이벤트를 인프로세스로 다시 발행한다.
 * <p>
 * 트랜잭션 밖에서 발행하고 리스너({@code @EventListener})는 이 스레드에서 동기로 실행된다. 리스너가 예외 없이 끝나야
 * 발행 완료로 표시하므로, 리스너가 실패하면 다음 폴링에서 다시 발행된다(at-least-once). 리스너가 끝난 뒤 완료 표시 전에
 * 서버가 내려가면 같은 이벤트가 한 번 더 발행될 수 있다.
 * <p>
 * 환경마다 인스턴스가 하나라고 보고 행을 선점하지 않는다. 인스턴스를 늘리면 두 릴레이가 같은 행을 발행하지 않도록 선점 방식을 넣어야 한다.
 */
@Component
@RequiredArgsConstructor
public class OutboxRelay {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelay.class);

    private static final long POLL_INTERVAL_MILLIS = 1000;
    private static final int MAX_ATTEMPTS = 5;

    private final OutboxEventJpaRepository outboxEventJpaRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final JsonMapper jsonMapper;

    @Scheduled(fixedDelay = POLL_INTERVAL_MILLIS)
    public void relay() {
        outboxEventJpaRepository.findTop100ByStatusOrderByIdAsc(OutboxStatus.PENDING)
            .forEach(this::relay);
    }

    // 한 이벤트가 실패해도 다음 이벤트는 계속 발행한다. 완료·실패 표시는 save가 건별 트랜잭션으로 반영한다
    private void relay(OutboxEventJpaEntity outboxEvent) {
        try {
            applicationEventPublisher.publishEvent(deserialize(outboxEvent));
            outboxEvent.markPublished(LocalDateTime.now(DateUtil.KST));
        } catch (RuntimeException e) {
            logFailure(outboxEvent, outboxEvent.recordFailure(MAX_ATTEMPTS), e);
        }
        outboxEventJpaRepository.save(outboxEvent);
    }

    private Object deserialize(OutboxEventJpaEntity outboxEvent) {
        try {
            Class<?> eventType = ClassUtils.forName(outboxEvent.getEventType(), ClassUtils.getDefaultClassLoader());
            return jsonMapper.readValue(outboxEvent.getPayload(), eventType);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("아웃박스 이벤트 타입을 찾을 수 없습니다: " + outboxEvent.getEventType(), e);
        }
    }

    private void logFailure(OutboxEventJpaEntity outboxEvent, boolean gaveUp, RuntimeException e) {
        if (gaveUp) {
            log.error("아웃박스 이벤트 발행을 포기함(FAILED): outboxEventId={}, eventType={}, attempts={}",
                outboxEvent.getId(), outboxEvent.getEventType(), outboxEvent.getAttempts(), e);
            return;
        }
        log.warn("아웃박스 이벤트 발행 실패(재시도 예정): outboxEventId={}, eventType={}, attempts={}",
            outboxEvent.getId(), outboxEvent.getEventType(), outboxEvent.getAttempts(), e);
    }
}
