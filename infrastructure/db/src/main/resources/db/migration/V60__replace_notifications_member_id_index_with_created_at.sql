-- 알림함 조회는 항상
-- WHERE member_id = ? ORDER BY created_at DESC, notification_id DESC 로 조회한다(운영진 공용 알림은 member_id IS NULL).
-- 필터 컬럼을 앞, 정렬 컬럼을 뒤에 둔 복합 인덱스로 바꾼다.
-- 새 인덱스를 먼저 만들고 기존 인덱스를 지워서 그사이 인덱스가 비는 구간이 없게 한다.
-- 기존 idx_notifications_member_id는 새 인덱스의 좌측 prefix라 완전히 포함되므로 제거한다.
-- InnoDB가 세컨더리 인덱스 끝에 PK(notification_id)를 붙이므로 보조 정렬 키까지 커버된다.
CREATE INDEX idx_notifications_member_id_created_at ON notifications (member_id, created_at);

DROP INDEX idx_notifications_member_id ON notifications;
