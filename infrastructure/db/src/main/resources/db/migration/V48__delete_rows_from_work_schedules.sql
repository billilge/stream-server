-- 요일만 저장된 근무 시간은 어느 주의 근무인지 알 수 없어 근무일로 옮길 수 없다.
-- 이 테이블에 쓰는 API도 아직 없었으므로 비우고, 바로 다음 마이그레이션에서 근무일 기준으로 바꾼다.
-- ⚠️ 적용 전 SELECT COUNT(*) FROM work_schedules 로 행이 있는지 확인한다.
DELETE FROM work_schedules;
