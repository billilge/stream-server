ALTER TABLE events
    ADD COLUMN is_published TINYINT(1) NOT NULL DEFAULT 0;

-- 사용자 행사 목록: WHERE is_published = 1 AND is_deleted = 0 ORDER BY event_start_at, event_id
-- (InnoDB가 세컨더리 인덱스 끝에 PK(event_id)를 붙이므로 보조 정렬 키까지 커버된다)
CREATE INDEX idx_events_is_published_is_deleted_event_start_at
    ON events (is_published, is_deleted, event_start_at);
