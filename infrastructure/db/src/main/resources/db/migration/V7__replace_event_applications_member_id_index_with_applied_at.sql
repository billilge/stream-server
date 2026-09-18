-- 내 행사 신청 내역 목록은 항상
-- WHERE member_id = ? ORDER BY applied_at DESC, event_application_id DESC 로 조회한다.
-- 필터 컬럼을 앞, 정렬 컬럼을 뒤에 둔 복합 인덱스로 바꾼다.
-- 기존 idx_event_applications_member_id는 새 인덱스의 좌측 prefix라 완전히 포함되므로 제거한다.
-- InnoDB가 세컨더리 인덱스 끝에 PK(event_application_id)를 붙이므로 보조 정렬 키까지 커버된다.
DROP INDEX idx_event_applications_member_id ON event_applications;

CREATE INDEX idx_event_applications_member_id_applied_at
    ON event_applications (member_id, applied_at);
