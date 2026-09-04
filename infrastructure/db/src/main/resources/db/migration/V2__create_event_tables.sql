CREATE TABLE events (
    event_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    target VARCHAR(255) NOT NULL,
    place VARCHAR(255) NULL,
    event_start_at DATETIME NOT NULL,
    event_end_at DATETIME NULL,
    apply_start_at DATETIME NOT NULL,
    apply_end_at DATETIME NOT NULL,
    recruit_type VARCHAR(30) NOT NULL,              -- FIRST_COME / OPEN
    image_ids JSON NULL,
    capacity INT NOT NULL,
    recruit_status VARCHAR(30) NOT NULL DEFAULT 'BEFORE_OPEN', -- BEFORE_OPEN / OPEN / CLOSED
    created_by BIGINT NOT NULL,
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE event_questions (
    event_question_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    question_text VARCHAR(500) NOT NULL,
    question_type VARCHAR(30) NOT NULL,             -- SHORT_TEXT / LONG_TEXT / SINGLE_CHOICE / MULTIPLE_CHOICE
    is_required TINYINT(1) NOT NULL DEFAULT 0,
    display_order INT NOT NULL,
    options JSON NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE INDEX idx_event_questions_event_id ON event_questions (event_id);

CREATE TABLE event_applications (
    event_application_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'APPLIED',  -- APPLIED / CANCELED
    applied_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    canceled_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE INDEX idx_event_applications_event_id_member_id ON event_applications (event_id, member_id);
CREATE INDEX idx_event_applications_member_id ON event_applications (member_id);

CREATE TABLE event_application_answers (
    event_application_answer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_application_id BIGINT NOT NULL,
    event_question_id BIGINT NOT NULL,
    answer_text TEXT NULL,
    selected_options JSON NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE INDEX idx_event_application_answers_event_application_id
    ON event_application_answers (event_application_id);

CREATE TABLE lockers (
    locker_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    locker_number VARCHAR(20) NOT NULL,
    section VARCHAR(20) NOT NULL,
    row_no INT NOT NULL,
    column_no INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE', -- AVAILABLE / DISABLED
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE locker_periods (
    locker_period_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    apply_start_at DATETIME NOT NULL,
    apply_end_at DATETIME NOT NULL,
    usage_start_at DATE NOT NULL,
    usage_end_at DATE NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE locker_applications (
    locker_application_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    locker_period_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    locker_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_locker_applications_locker_period_id_locker_id
    ON locker_applications (locker_period_id, locker_id);
CREATE INDEX idx_locker_applications_member_id ON locker_applications (member_id);

CREATE TABLE archives (
    archive_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NULL,
    location VARCHAR(255) NULL,
    department_name VARCHAR(100) NOT NULL,
    image_ids JSON NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE archive_related_links (
    archive_related_link_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    archive_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    url VARCHAR(2048) NOT NULL,
    display_order INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE INDEX idx_archive_related_links_archive_id ON archive_related_links (archive_id);
