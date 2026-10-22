-- 행사 대상 문구(target)는 관리자 등록 화면에서 받지 않기로 해 컬럼을 없앤다.
-- 기존 값은 버린다.
ALTER TABLE events DROP COLUMN target;
