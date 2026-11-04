-- 학생회비 납부 요청을 회원당 한 행(현재 상태)에서 요청당 한 행(이력)으로 바꾸고, 요청에 담기는 값을 추가한다.
-- 회원의 지금 상태는 최신 행(PK가 가장 큰 행)의 상태이고, 요청 시각은 행이 생긴 시각(created_at)으로 갈음한다.
--
-- 새 컬럼은 이 마이그레이션 전에 생긴 행에는 값이 없어 NULL을 허용한다.
--   remaining_semesters: 학생이 입력한 남은 학기 수(1~8)
--   amount: 요청 시점에 안내한 납부 금액(남은 학기 수 × 그때의 학기당 회비). 계산 결과가 long이라 BIGINT로 둔다
--   proof_image_key: 남은 학기 수의 근거로 올린 학점이수현황 캡처의 file_key
--   reject_reason: 운영진이 확인필요(REJECTED)로 돌려보내며 학생에게 남긴 반려 사유
ALTER TABLE student_transfer_statuses
    ADD COLUMN remaining_semesters INT NULL AFTER status,
    ADD COLUMN amount BIGINT NULL AFTER remaining_semesters,
    ADD COLUMN proof_image_key VARCHAR(255) NULL AFTER amount,
    ADD COLUMN reject_reason VARCHAR(255) NULL AFTER proof_image_key;

-- 상태는 PENDING / PAID / REJECTED다. 한 번도 요청하지 않은 회원은 행이 없으므로 기본값 UNPAID를 없앤다.
-- DEFAULT를 남기면 INSERT가 status를 빠뜨려도 조용히 UNPAID로 들어간다(V10과 같은 이유). 기존 UNPAID 행은 V23이 정리한다.
ALTER TABLE student_transfer_statuses
    ALTER COLUMN status DROP DEFAULT;

-- 최신 요청 조회(WHERE member_id = ? ORDER BY student_transfer_status_id DESC)용.
-- InnoDB가 세컨더리 인덱스 끝에 PK를 붙이므로 member_id만으로 정렬까지 받쳐진다.
-- 회원당 유니크를 지우기 전에 먼저 만들어 member_id 조회가 인덱스를 잃는 순간이 없게 한다.
CREATE INDEX idx_student_transfer_statuses_member_id ON student_transfer_statuses (member_id);

DROP INDEX uk_student_transfer_statuses_member_id ON student_transfer_statuses;

-- 진행 중(PENDING)·완료(PAID) 요청은 회원당 하나만 허용한다. 반려(REJECTED) 행은 NULL이 되어 몇 개든 남는다
-- (MySQL 유니크는 NULL을 여러 개 허용한다). 같은 회원의 동시 요청은 이 유니크가 두 번째 INSERT를 막는다.
ALTER TABLE student_transfer_statuses
    ADD COLUMN active_member_id BIGINT
        GENERATED ALWAYS AS (IF(status IN ('PENDING', 'PAID'), member_id, NULL)) VIRTUAL;
CREATE UNIQUE INDEX uk_student_transfer_statuses_active_member_id
    ON student_transfer_statuses (active_member_id);
