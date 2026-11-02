-- 운영진이 신청을 취소할 수 있게 한다. 취소해도 행은 남긴다 — 운영진이 그 칸이 왜 비었는지 알아야 하고,
-- 지난 신청 이력도 남아야 한다.
--
-- 승인 절차 없이 신청이 곧 배정이라 값은 "살아 있음/취소됨" 둘뿐이다. 그래서 status 문자열이 아니라 불리언으로 둔다.
-- "기간종료"는 저장하지 않고 운영 회차로 계산한다(LockerApplicationStatus).
--
-- ⚠️ 유니크를 갈아끼우는 동안(DROP 직후 CREATE 사이) 같은 칸 신청이 두 건 들어오면 둘 다 커밋되고
--    그 뒤 인덱스 생성이 실패한다. 신청 기간 밖에 적용한다.
--    적용 전 SELECT COUNT(*) FROM locker_applications 로 행 수를 확인한다.
ALTER TABLE locker_applications
    ADD COLUMN is_canceled TINYINT(1) NOT NULL DEFAULT 0;

-- 취소된 신청이 재배정을 막지 않도록 살아 있는 신청에만 유니크를 건다.
-- UNIQUE(col, is_canceled)로는 막을 수 없다 — 취소 행끼리의 중복이 그대로 허용된다. (flyway-migration.md 3-4절)
ALTER TABLE locker_applications
    ADD COLUMN active_member_key VARCHAR(64)
        GENERATED ALWAYS AS (IF(is_canceled = 0, CONCAT(locker_period_id, '-', member_id), NULL)) VIRTUAL,
    ADD COLUMN active_locker_key VARCHAR(64)
        GENERATED ALWAYS AS (IF(is_canceled = 0, CONCAT(locker_period_id, '-', locker_id), NULL)) VIRTUAL;

DROP INDEX uk_locker_applications_locker_period_id_member_id ON locker_applications;
DROP INDEX uk_locker_applications_locker_period_id_locker_id ON locker_applications;

CREATE UNIQUE INDEX uk_locker_applications_active_member_key
    ON locker_applications (active_member_key);
CREATE UNIQUE INDEX uk_locker_applications_active_locker_key
    ON locker_applications (active_locker_key);

-- 유니크를 generated column으로 옮기면서 (locker_period_id, locker_id) 접두 조회가 인덱스를 잃는다.
-- generated column 유니크는 CONCAT 문자열에 걸려 있어 이 조건으로는 탈 수 없다.
-- 이용 중 판정과 신청 여부 조회가 이 경로를 쓰고, 둘 다 취소된 신청을 빼야 하므로 is_canceled를 뒤에 단다(3-4절).
-- (V11이 반대 방향으로 했던 교체를 되돌리는 셈이다)
CREATE INDEX idx_locker_applications_locker_period_id_locker_id_is_canceled
    ON locker_applications (locker_period_id, locker_id, is_canceled);
