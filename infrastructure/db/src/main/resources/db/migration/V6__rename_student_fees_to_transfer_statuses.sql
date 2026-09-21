-- V3의 idx_student_fees_member_id는 유니크가 아니라, 동시 확인요청으로 같은 member_id에
-- row가 두 개 이상 쌓였을 수 있다. 아래 uk_student_transfer_statuses_member_id를 걸기 전에
-- member_id별로 가장 최근(id가 가장 큰) row만 남기고 나머지를 지운다.
DELETE t1 FROM student_fees t1
    INNER JOIN student_fees t2
        ON t1.member_id = t2.member_id AND t1.student_fee_id < t2.student_fee_id;

ALTER TABLE student_fees
    DROP COLUMN amount,
    DROP COLUMN payment_link_url,
    DROP COLUMN paid_at,
    DROP COLUMN confirmed_by,
    CHANGE COLUMN payment_status status VARCHAR(20) NOT NULL DEFAULT 'UNPAID', -- UNPAID / PENDING / PAID
    ADD COLUMN reviewed_at DATETIME NULL;

DROP INDEX idx_student_fees_member_id ON student_fees;

ALTER TABLE student_fees
    CHANGE COLUMN student_fee_id student_transfer_status_id BIGINT NOT NULL AUTO_INCREMENT;

RENAME TABLE student_fees TO student_transfer_statuses;

CREATE UNIQUE INDEX uk_student_transfer_statuses_member_id ON student_transfer_statuses (member_id);

ALTER TABLE members
    DROP COLUMN is_fee_paid;

-- 회원당 납부자 명부는 1행만 존재한다 (PayerService.sync가 member_id로 조회·갱신·삭제한다)
DROP INDEX idx_payers_member_id ON payers;
CREATE UNIQUE INDEX uk_payers_member_id ON payers (member_id);
