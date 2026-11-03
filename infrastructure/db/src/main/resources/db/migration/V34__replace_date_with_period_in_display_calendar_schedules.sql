-- 학사 일정을 날짜 하나 + 일정명 목록에서 일정 1건 = 일정명 + 기간(시작일~종료일)으로 바꾼다.
-- 관리자 화면이 일정을 기간으로 등록·수정하고, 여러 날에 걸친 일정을 한 건으로 다룬다.
-- 하루 일정은 시작일과 종료일이 같다.
--
-- 앞선 마이그레이션(delete_rows_from_display_tables)에서 행을 모두 지웠으므로 NOT NULL 컬럼에 DEFAULT를 두지 않는다.
-- 학사 일정은 1년에 수십 건이라 기간 겹침 조회(start_date <= ? AND end_date >= ?)용 인덱스는 두지 않는다.
ALTER TABLE display_calendar_schedules
    DROP COLUMN date,
    DROP COLUMN schedules,
    ADD COLUMN title VARCHAR(100) NOT NULL AFTER id,
    ADD COLUMN start_date DATE NOT NULL AFTER title,
    ADD COLUMN end_date DATE NOT NULL AFTER start_date;
