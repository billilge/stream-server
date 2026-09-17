DROP INDEX idx_notices_category_is_deleted ON notices;

-- 목록 조회: WHERE (category = ? 또는 전체) AND is_deleted = 0 ORDER BY pinned DESC, created_at DESC, notice_id DESC
-- (InnoDB가 세컨더리 인덱스 끝에 PK(notice_id)를 붙이므로 보조 정렬 키까지 커버된다)
CREATE INDEX idx_notices_category_is_deleted_pinned_created_at
    ON notices (category, is_deleted, pinned, created_at);
