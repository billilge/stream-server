CREATE TABLE chat_faqs (
    chat_faq_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question VARCHAR(100) NOT NULL,
    slot INT NOT NULL,                          -- 개수 상한을 지키려는 자리 번호(0~4). 노출 순서는 등록 순이다
    is_deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- 활성 행만 자리를 점유한다(flyway-migration.md 3-4절)
    active_slot INT GENERATED ALWAYS AS (IF(is_deleted = 0, slot, NULL)) VIRTUAL
);

CREATE UNIQUE INDEX uk_chat_faqs_active_slot ON chat_faqs (active_slot);
