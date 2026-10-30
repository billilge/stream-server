-- 근무 시간표는 주마다 달라서 요일 대신 근무일을 저장한다.
ALTER TABLE work_schedules
    DROP COLUMN day_of_week,
    ADD COLUMN work_date DATE NOT NULL AFTER member_id;

-- 주 단위 범위 조회(work_date BETWEEN 월 AND 금)와 겹침 검사(work_date IN ...)용
CREATE INDEX idx_work_schedules_work_date ON work_schedules (work_date);
