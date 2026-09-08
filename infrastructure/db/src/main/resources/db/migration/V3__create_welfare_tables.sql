CREATE TABLE items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(30) NOT NULL,                      -- CONSUMABLE / RENTAL
    count INT NOT NULL,
    image_key VARCHAR(255) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE rental_histories (
    history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    worker_id BIGINT NULL,
    item_code VARCHAR(255) NULL,
    rental_status VARCHAR(30) NOT NULL,             -- CANCEL / CONFIRMED / PENDING / REJECTED / RENTAL / RETURNED / RETURN_CONFIRMED / RETURN_PENDING
    rented_count INT NOT NULL,
    applied_at DATETIME NOT NULL,
    rent_at DATETIME NULL,
    returned_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE INDEX idx_rental_histories_member_id ON rental_histories (member_id);
CREATE INDEX idx_rental_histories_item_id ON rental_histories (item_id);

CREATE TABLE rental_status_worker_logs (
    rental_status_worker_log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rental_history_id BIGINT NOT NULL,
    worker_id BIGINT NOT NULL,
    rental_status VARCHAR(30) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_rental_status_worker_logs_rental_history_id
    ON rental_status_worker_logs (rental_history_id);

CREATE TABLE student_fees (
    student_fee_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    amount INT NOT NULL,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID', -- UNPAID / PAID
    payment_link_url VARCHAR(500) NULL,
    paid_at DATETIME NULL,
    confirmed_by BIGINT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE INDEX idx_student_fees_member_id ON student_fees (member_id);

CREATE TABLE payers (
    payer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    student_id VARCHAR(255) NULL,
    enrollment_year VARCHAR(255) NOT NULL,
    registered TINYINT(1) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE INDEX idx_payers_member_id ON payers (member_id);

CREATE TABLE notices (
    notice_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(30) NOT NULL,                  -- GENERAL / PARTNERSHIP
    pinned TINYINT(1) NOT NULL DEFAULT 0,
    created_by BIGINT NULL,
    attachment_ids JSON NULL,
    image_ids JSON NULL,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE INDEX idx_notices_category_is_deleted ON notices (category, is_deleted);

CREATE TABLE chat_messages (
    message_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    sender_type VARCHAR(10) NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_chat_messages_member_id ON chat_messages (member_id);

CREATE TABLE open_feedbacks (
    feedback_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    round INT NOT NULL,
    category VARCHAR(20) NOT NULL,
    question VARCHAR(1000) NOT NULL,
    answer TEXT NULL,
    answered_at DATETIME NULL,
    created_by BIGINT NOT NULL,
    answered_by BIGINT NULL,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
