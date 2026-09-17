ALTER TABLE student_fees
    DROP COLUMN amount,
    DROP COLUMN payment_link_url,
    DROP COLUMN paid_at,
    DROP COLUMN confirmed_by,
    CHANGE COLUMN payment_status status VARCHAR(20) NOT NULL DEFAULT 'UNPAID', -- UNPAID / PENDING / PAID
    ADD COLUMN reviewed_at DATETIME NULL;

DROP INDEX idx_student_fees_member_id ON student_fees;

ALTER TABLE student_fees
    CHANGE COLUMN student_fee_id student_transfer_request_id BIGINT NOT NULL AUTO_INCREMENT;

RENAME TABLE student_fees TO student_transfer_requests;

CREATE UNIQUE INDEX uk_student_transfer_requests_member_id ON student_transfer_requests (member_id);

ALTER TABLE members
    DROP COLUMN is_fee_paid;
