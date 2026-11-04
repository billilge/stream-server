-- 디스플레이 포스터·학사 일정을 새 관리자 화면 형식으로 바꾸기 전에 두 테이블을 비운다.
-- 컬럼 교체는 바로 다음 두 마이그레이션(display_posters, display_calendar_schedules 순)에서 한다.
--
-- 레거시 형식으로 들어간 행은 새 형식으로 옮길 수 없다.
-- 포스터의 image_key는 files 행이 없어 image_file_id로 바꿀 수 없고,
-- 일정의 schedules(날짜 하나에 일정명을 | 로 이어 붙인 문자열)는 일정명 + 기간 형식이 아니다.
-- 두 테이블에 쓰는 API도 아직 없었으므로 지우고, 필요한 포스터·일정은 관리자 화면에서 다시 등록한다.
-- ⚠️ 적용 전 SELECT COUNT(*) FROM display_posters, SELECT COUNT(*) FROM display_calendar_schedules 로 행이 있는지 확인한다.
-- ⚠️ 다음 두 마이그레이션까지 적용한 뒤에는 이전 버전 이미지로 되돌릴 수 없다(ddl-auto: validate에서 기동 실패).
DELETE FROM display_posters;
DELETE FROM display_calendar_schedules;
