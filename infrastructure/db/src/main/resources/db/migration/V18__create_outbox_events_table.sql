-- 도메인 이벤트를 원 비즈니스 트랜잭션과 함께 기록하는 아웃박스. 릴레이(infrastructure:outbox)가 PENDING을 읽어 다시 발행한다.
-- 리스너가 계속 실패하는 이벤트는 attempts가 한도에 닿으면 FAILED로 바꿔 더는 재시도하지 않는다.
CREATE TABLE outbox_events (
    outbox_event_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(200) NOT NULL,                -- 이벤트 클래스 FQCN. 역직렬화에 쓴다
    payload JSON NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',   -- PENDING / PUBLISHED / FAILED
    attempts INT NOT NULL DEFAULT 0,                 -- 발행에 실패한 횟수
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at DATETIME NULL
);
CREATE INDEX idx_outbox_events_status_outbox_event_id ON outbox_events (status, outbox_event_id);
