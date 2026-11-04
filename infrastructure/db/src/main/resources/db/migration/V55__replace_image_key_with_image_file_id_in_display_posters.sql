-- 포스터 이미지를 presigned 업로드로 받고 파일 아이디를 저장한다.
-- 다른 이미지 컬럼(notices.image_ids, events.image_ids, archives.image_ids, locker_section_layouts.photo_file_ids)과 같다.
-- 포스터를 지우거나 이미지를 바꿀 때 서버가 이전 파일을 지우려면 파일 아이디가 있어야 한다(AdminDisplayPosterWriteUseCase).
--
-- 바로 앞 마이그레이션(delete_rows_from_display_tables)에서 행을 모두 지웠으므로 NOT NULL 컬럼에 DEFAULT를 두지 않는다.
ALTER TABLE display_posters
    DROP COLUMN image_key,
    ADD COLUMN image_file_id BIGINT NOT NULL AFTER title; -- files.file_id. 공개 URL은 웹 계층에서 만든다
