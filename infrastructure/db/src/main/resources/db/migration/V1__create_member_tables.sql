CREATE TABLE members (
    member_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    department VARCHAR(30) NOT NULL,            -- AI / SW
    email VARCHAR(255) NULL,
    fcm_token VARCHAR(255) NULL,
    is_fee_paid TINYINT(1) NOT NULL DEFAULT 0,
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT', -- STUDENT / ADMIN
    council_department VARCHAR(30) NULL,        -- CouncilDepartment.name()
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 활성 학번만 유니크 (삭제 후 재등록 허용)
ALTER TABLE members
    ADD COLUMN active_student_id VARCHAR(255)
        GENERATED ALWAYS AS (IF(is_deleted = 0, student_id, NULL)) VIRTUAL;
CREATE UNIQUE INDEX uk_members_active_student_id ON members (active_student_id);

CREATE TABLE member_term_agreements (
    member_term_agreement_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    term_type VARCHAR(30) NOT NULL,             -- PRIVACY_POLICY / TERMS_OF_SERVICE
    term_version VARCHAR(20) NOT NULL,
    agreed TINYINT(1) NOT NULL DEFAULT 0,
    agreed_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uk_member_term_agreements_member_id_term_type
    ON member_term_agreements (member_id, term_type);

CREATE TABLE member_notification_settings (
    member_notification_setting_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    rental_enabled TINYINT(1) NOT NULL DEFAULT 1,
    event_enabled TINYINT(1) NOT NULL DEFAULT 1,
    locker_enabled TINYINT(1) NOT NULL DEFAULT 1,
    notice_enabled TINYINT(1) NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uk_member_notification_settings_member_id
    ON member_notification_settings (member_id);

CREATE TABLE notifications (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    format_values VARCHAR(255) NULL,
    is_read TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_notifications_member_id ON notifications (member_id);
