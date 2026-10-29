package kr.ac.kookmin.stream.common;

import kr.ac.kookmin.stream.common.event.DomainEvent;

/**
 * 도메인 이벤트를 아웃박스에 기록한다. 구현은 {@code infrastructure:outbox}가 맡는다.
 * <p>
 * 원 비즈니스 쓰기와 같은 트랜잭션 안에서 불러야 한다. 트랜잭션이 롤백되면 기록도 함께 사라지고,
 * 커밋되면 릴레이가 이벤트를 다시 발행한다. 트랜잭션 밖에서 부르면 실패한다.
 */
public interface OutboxWriter {
    void record(DomainEvent event);
}
