-- 강제 마감 기능을 없앤다. recruit_status는 'CLOSED'일 때 강제 마감이라는 뜻만 가졌고,
-- 모집 상태는 이제 신청 기간과 잔여 정원으로만 계산한다.
ALTER TABLE events DROP COLUMN recruit_status;
