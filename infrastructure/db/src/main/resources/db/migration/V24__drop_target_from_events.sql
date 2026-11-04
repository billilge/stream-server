-- 행사 대상 문구(target)는 관리자 등록 화면에서 받지 않기로 해 컬럼을 없앤다.
-- 기존 값은 버린다.
-- ⚠️ 적용 전 SELECT COUNT(*) FROM events 로 행이 있는지 확인한다.
-- ⚠️ 적용 후에는 이전 버전 이미지로 되돌릴 수 없다(ddl-auto: validate에서 기동 실패).
ALTER TABLE events DROP COLUMN target;
