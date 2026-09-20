DROP INDEX idx_notices_category_is_deleted ON notices;

-- 목록 조회는 항상 WHERE is_deleted = 0 ORDER BY pinned DESC, created_at DESC, notice_id DESC이고,
-- category는 있을 수도/없을 수도 있는 선택적 필터다. category를 앞에 두면 category 없는 조회에서
-- 정렬 순서 자체를 인덱스가 보장 못 해 filesort가 발생하므로, is_deleted를 앞에 둬 두 경우 모두
-- 정렬은 인덱스로 커버한다. category(값 2개뿐, 저카디널리티)로 미리 좁히는 이점만 포기한다.
-- (InnoDB가 세컨더리 인덱스 끝에 PK(notice_id)를 붙이므로 보조 정렬 키까지 커버된다)
CREATE INDEX idx_notices_is_deleted_pinned_created_at
    ON notices (is_deleted, pinned, created_at);
