-- 구역 사진을 여러 장 저장한다. 구역당 한 장 전제로 V15에서 단일 컬럼을 뒀는데, 실제로 두 장인 구역이 있다.
-- 배열 순서가 노출 순서다.
--
-- 저장소의 다른 파일 목록 컬럼(notices.image_ids·attachment_ids, events.image_ids, archives.image_ids)과 같은
-- JSON NULL로 둔다. JSON으로 바꾸면 "반드시 한 장"은 DB가 못 지킨다 — 빈 배열은 NULL이 아니라서 NOT NULL을
-- 통과한다. 최소 한 장 검증은 구역 쓰기 API에서 한다.
--
-- ⚠️ 한 파일에 컬럼 추가·백필·DROP이 함께 있는데 MySQL은 DDL을 트랜잭션으로 묶지 않는다. 중간에 실패하면 앞 문장이
--    이미 적용된 채로 남아 손으로 되돌려야 한다.
--    그리고 photo_file_id를 지운 뒤에는 이전 이미지로 롤백할 수 없다 — ddl-auto: validate가 없는 컬럼을 찾아 기동을 막는다.
--    적용 전 SELECT COUNT(*) FROM locker_section_layouts 로 행 수를 확인한다. 0건이면 백필이 건드릴 행도 없다.
ALTER TABLE locker_section_layouts
    ADD COLUMN photo_file_ids JSON NULL;

UPDATE locker_section_layouts
SET photo_file_ids = JSON_ARRAY(photo_file_id);

ALTER TABLE locker_section_layouts
    DROP COLUMN photo_file_id;
