ALTER TABLE student_fees
    DROP COLUMN amount,
    DROP COLUMN payment_link_url,
    DROP COLUMN paid_at,
    DROP COLUMN confirmed_by,
    CHANGE COLUMN payment_status status VARCHAR(20) NOT NULL DEFAULT 'UNPAID', -- UNPAID / PENDING / PAID
    ADD COLUMN reviewed_at DATETIME NULL;

DROP INDEX idx_student_fees_member_id ON student_fees;
CREATE UNIQUE INDEX uk_student_fees_member_id ON student_fees (member_id);

ALTER TABLE members
    DROP COLUMN is_fee_paid;
