-- 납부 요청을 회원당 한 행(현재 상태)에서 요청당 한 행(이력)으로 바꾼다. 반려된 요청도 지우지 않고 남긴다.
-- 회원의 지금 상태는 최신 행(PK가 가장 큰 행)의 상태다.

-- 최신 요청 조회용. 회원당 유니크 인덱스를 지우기 전에 먼저 만들어 member_id 조회가 인덱스를 잃지 않게 한다.
CREATE INDEX idx_student_transfer_statuses_member_id_id
    ON student_transfer_statuses (member_id, student_transfer_status_id);

DROP INDEX uk_student_transfer_statuses_member_id ON student_transfer_statuses;

-- 진행 중(PENDING)·완료(PAID) 요청은 회원당 하나만 허용한다. 반려(REJECTED) 행은 NULL이 되어 몇 개든 남는다
-- (MySQL 유니크는 NULL을 여러 개 허용한다). 같은 회원의 동시 요청은 이 유니크가 두 번째 INSERT를 막는다.
-- 낙관적 락으로는 막을 수 없다 — 두 요청 모두 기존 행을 고치지 않고 새 행을 넣기만 하고, 첫 요청은 행 자체가 없다.
ALTER TABLE student_transfer_statuses
    ADD COLUMN active_member_id BIGINT
        GENERATED ALWAYS AS (IF(status IN ('PENDING', 'PAID'), member_id, NULL)) VIRTUAL;
CREATE UNIQUE INDEX uk_student_transfer_statuses_active_member_id
    ON student_transfer_statuses (active_member_id);
