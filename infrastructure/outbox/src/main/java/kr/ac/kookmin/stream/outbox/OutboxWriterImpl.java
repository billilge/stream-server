package kr.ac.kookmin.stream.outbox;

import kr.ac.kookmin.stream.common.OutboxWriter;
import kr.ac.kookmin.stream.common.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
public class OutboxWriterImpl implements OutboxWriter {

    private final OutboxEventJpaRepository outboxEventJpaRepository;
    private final JsonMapper jsonMapper;

    // 원 비즈니스 쓰기와 함께 커밋·롤백되어야 하므로 진행 중인 트랜잭션이 없으면 실패시킨다
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void record(DomainEvent event) {
        String payload = jsonMapper.writeValueAsString(event);
        outboxEventJpaRepository.save(OutboxEventJpaEntity.pending(event.getClass().getName(), payload));
    }
}
