-- 구역 사진을 여러 장 저장한다. 구역당 한 장 전제로 V15에서 단일 컬럼을 뒀는데, 실제로 두 장인 구역이 있다.
-- 배열 순서가 노출 순서다.
--
-- 저장소의 다른 파일 목록 컬럼(notices.image_ids·attachment_ids, events.image_ids, archives.image_ids)과 같은
-- JSON NULL로 둔다. JSON으로 바꾸면 "반드시 한 장"은 DB가 못 지킨다 — 빈 배열은 NULL이 아니라서 NOT NULL을
-- 통과한다. 최소 한 장 검증은 구역 쓰기 API에서 한다.
ALTER TABLE locker_section_layouts
    ADD COLUMN photo_file_ids JSON NULL;

UPDATE locker_section_layouts
SET photo_file_ids = JSON_ARRAY(photo_file_id);

ALTER TABLE locker_section_layouts
    DROP COLUMN photo_file_id;
