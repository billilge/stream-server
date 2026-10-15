-- 학생이 제출하는 납부 정보(남은 학기 수·학점이수현황 캡처)와, 관리자 안내 문구·송금 확인 요청 시각을 담는다.
-- status에 NEEDS_CHECK(확인필요)가 추가된다 — UNPAID / PENDING / PAID / NEEDS_CHECK.
-- 기존 행은 제출 전으로 보므로 전부 NULL을 허용한다.
ALTER TABLE student_transfer_statuses
    ADD COLUMN remaining_semesters INT NULL,
    ADD COLUMN proof_image_key VARCHAR(255) NULL,
    ADD COLUMN admin_note VARCHAR(255) NULL,
    ADD COLUMN requested_at DATETIME NULL;


-- proof_image_key로 파일을 찾기 위한 인덱스. files.file_key가 TEXT라 prefix 길이를 지정한다.
CREATE INDEX idx_files_file_key ON files (file_key(255));
