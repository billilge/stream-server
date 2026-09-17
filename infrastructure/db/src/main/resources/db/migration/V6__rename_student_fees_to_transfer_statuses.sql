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
