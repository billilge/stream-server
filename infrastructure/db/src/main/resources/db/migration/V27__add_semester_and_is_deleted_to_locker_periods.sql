-- 관리자 API가 회차를 등록·삭제할 수 있게 한다.
--
-- 연도·학기: 등록 화면이 연도와 학기를 받고 이름(예: 2026-1학기)은 서버가 만든다. 상세 조회에서 둘을 따로 내려줘야 해서
-- 이름에서 파싱하지 않고 컬럼으로 둔다. 운영 중인 행이 있어 DEFAULT를 주고(3-3절) 아래에서 이름으로 채운다.
-- YEAR는 MySQL 데이터 타입 이름이라 컬럼명을 period_year로 둔다.
-- 삭제: 실수로 지운 회차를 되살릴 수 있도록 행은 남긴다. 회차는 한 학기에 한두 건이라 인덱스는 두지 않는다.
ALTER TABLE locker_periods
    ADD COLUMN period_year SMALLINT NOT NULL DEFAULT 0,
    ADD COLUMN semester TINYINT NOT NULL DEFAULT 0,
    ADD COLUMN is_deleted TINYINT(1) NOT NULL DEFAULT 0;

-- 이 컬럼이 생기기 전에 DB로 직접 넣은 회차(V15 주석 참고)는 이름이 "2026-2학기" 형식이다. 거기서 채운다.
--
-- 형식이 다른 행은 0으로 남는다. 수정 API가 연도·학기를 바꾸지 않으므로 그런 행은 DB에서 직접 고쳐야 한다.
-- 그리고 0으로 남는 행이 둘 이상이면 아래 유니크 인덱스가 중복('0-0')으로 실패해 이 마이그레이션 전체가 멈춘다.
-- 적용 전에 SELECT locker_period_id, name FROM locker_periods; 로 행 수와 이름 형식을 확인한다.
UPDATE locker_periods
SET period_year = CAST(SUBSTRING_INDEX(name, '-', 1) AS UNSIGNED),
    semester = CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(name, '-', -1), '학기', 1) AS UNSIGNED)
WHERE name REGEXP '^[0-9]{4}-[12]학기$';

-- 같은 학기 회차는 하나만 둔다. 삭제한 회차가 학기를 점유하지 않도록 활성 행에만 건다 (flyway-migration.md 3-4절).
ALTER TABLE locker_periods
    ADD COLUMN active_semester VARCHAR(10)
        GENERATED ALWAYS AS (IF(is_deleted = 0, CONCAT(period_year, '-', semester), NULL)) VIRTUAL;
CREATE UNIQUE INDEX uk_locker_periods_active_semester ON locker_periods (active_semester);
