-- 신청 일시를 앱이 정해 저장한다. 행사 신청(event_applications.applied_at)과 같은 방식으로 맞춘다.
-- created_at은 DB 기본값으로만 채워져(insertable = false) 저장 직후 앱이 값을 알 수 없고, 기록용으로 그대로 둔다.
-- 기존 행은 created_at으로 채운다.
ALTER TABLE locker_applications
    ADD COLUMN applied_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;
UPDATE locker_applications SET applied_at = created_at;

-- 내 사물함 신청 내역은 WHERE member_id = ? ORDER BY applied_at DESC, locker_application_id DESC 로 조회한다.
-- 기존 idx_locker_applications_member_id는 새 인덱스의 좌측 prefix라 완전히 포함되므로 제거한다. (V7과 같은 패턴)
DROP INDEX idx_locker_applications_member_id ON locker_applications;
CREATE INDEX idx_locker_applications_member_id_applied_at
    ON locker_applications (member_id, applied_at);

-- 같은 회차에 한 사물함은 한 명에게만 배정된다. 동시 신청은 이 제약이 막아 먼저 커밋된 신청만 남는다.
-- 적용 전 중복이 없는지 확인한다:
--   SELECT locker_period_id, locker_id, COUNT(*) FROM locker_applications GROUP BY 1, 2 HAVING COUNT(*) > 1;
-- 유니크 인덱스가 같은 컬럼의 조회를 대신하므로 기존 비유니크 인덱스는 교체한다.
DROP INDEX idx_locker_applications_locker_period_id_locker_id ON locker_applications;
CREATE UNIQUE INDEX uk_locker_applications_locker_period_id_locker_id
    ON locker_applications (locker_period_id, locker_id);
