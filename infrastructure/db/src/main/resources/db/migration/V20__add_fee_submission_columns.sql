-- 학생이 제출하는 납부 정보(남은 학기 수·학점이수현황 캡처)와, 관리자 안내 문구·송금 확인 요청 시각을 담는다.
-- status에 NEEDS_CHECK(확인필요)가 추가된다 — UNPAID / PENDING / PAID / NEEDS_CHECK.
-- 기존 행은 제출 전으로 보므로 전부 NULL을 허용한다.
ALTER TABLE student_transfer_statuses
    ADD COLUMN remaining_semesters INT NULL,
    ADD COLUMN proof_image_key VARCHAR(255) NULL,
    ADD COLUMN admin_note VARCHAR(255) NULL,
    ADD COLUMN requested_at DATETIME NULL;

-- 관리자 납부자 목록은 "납부 전" 회원까지 보여줘야 한다. 그런 회원은 student_transfer_statuses에
-- 행이 없으므로 회원 기준으로 검색·필터·페이징하려면 상태를 회원 쪽에도 둬야 한다.
-- student_transfer_statuses.status와 항상 같은 트랜잭션에서 갱신한다.
ALTER TABLE members
    ADD COLUMN fee_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID';

-- 실제 조회는 WHERE fee_status = ? AND is_deleted = 0 형태다. 필터 컬럼을 앞에, soft delete를 뒤에 둔다.
CREATE INDEX idx_members_fee_status_is_deleted ON members (fee_status, is_deleted);

-- proof_image_key로 파일을 찾기 위한 인덱스. files.file_key가 TEXT라 prefix 길이를 지정한다.
CREATE INDEX idx_files_file_key ON files (file_key(255));
