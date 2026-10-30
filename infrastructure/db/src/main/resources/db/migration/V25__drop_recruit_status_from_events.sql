-- 강제 마감 기능을 없앤다. recruit_status는 'CLOSED'일 때 강제 마감이라는 뜻만 가졌고,
-- 모집 상태는 이제 신청 기간과 잔여 정원으로만 계산한다.
-- ⚠️ 적용 전 SELECT COUNT(*) FROM events 로 행이 있는지 확인한다.
-- ⚠️ 적용 후에는 이전 버전 이미지로 되돌릴 수 없다(ddl-auto: validate에서 기동 실패).
ALTER TABLE events DROP COLUMN recruit_status;
