-- 사물함 구역을 lockers.section 문자열에서 별도 테이블로 승격하고, 운영 회차에 게시 여부를 추가한다.
--
-- ⚠️ lockers에 운영 데이터가 없다는 전제다. 관리자 사물함 등록 API가 아직 없어 행이 쌓일 경로가 없고,
--    locker_label은 관리자가 직접 입력하는 값이라 기존 행에서 유도할 수 없다.
--    적용 전 SELECT COUNT(*) FROM lockers 로 확인한다.

CREATE TABLE locker_sections (
    locker_section_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    label VARCHAR(20) NOT NULL,                 -- 화면에 표시할 구역 라벨. "A-1", "A-2", "C"
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uk_locker_sections_label ON locker_sections (label);

-- locker_number를 INT로 바꾼다. 사물함 번호는 블록 안에서 이어지는 순번이라(A-1이 38번까지면 A-2는 39번부터)
-- 문자열로 두면 정렬이 사전순("1","10","2")이 되고 번호 범위를 다루기 어렵다.
-- locker_label("A-37")은 번호에서 유도하지 않고 관리자가 직접 입력한다.
ALTER TABLE lockers
    ADD COLUMN section_id BIGINT NOT NULL,
    ADD COLUMN locker_label VARCHAR(20) NOT NULL,
    MODIFY COLUMN locker_number INT NOT NULL,
    DROP COLUMN section;

-- 구역 상세 조회: WHERE section_id = ? AND is_deleted = 0 ORDER BY row_no, column_no
-- 필터 컬럼을 앞, 정렬 컬럼을 뒤에 둬 filesort 없이 배치도 순서대로 읽는다.
CREATE INDEX idx_lockers_section_id_is_deleted_row_no_column_no
    ON lockers (section_id, is_deleted, row_no, column_no);

-- 한 회차에 회원당 사물함 한 개만 신청할 수 있고 수정·취소가 없다. 신청 건수 제한을 DB 제약으로 못박는다.
-- 취소가 없어 상태 컬럼이 없으므로 행 존재 자체가 신청이고, soft delete용 generated column 패턴도 필요 없다.
-- 기존 idx_locker_applications_member_id는 유니크의 좌측 prefix가 아니라(회차가 앞) 회원 단독 조회에 여전히 쓰여 남긴다.
CREATE UNIQUE INDEX uk_locker_applications_locker_period_id_member_id
    ON locker_applications (locker_period_id, member_id);

-- 운영진이 준비 중인 회차가 학생에게 노출되지 않도록 게시 여부를 둔다.
-- events(V5)와 같은 방식이며, 관리자 게시 API가 생기기 전까지는 DB에서 직접 켜야 한다.
ALTER TABLE locker_periods
    ADD COLUMN is_published TINYINT(1) NOT NULL DEFAULT 0;
