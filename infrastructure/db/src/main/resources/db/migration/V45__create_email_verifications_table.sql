-- 회원가입 이메일 인증 코드 발송 기록. 발송할 때마다 행을 쌓고, 인증 여부는 회원의 가장 최근 행으로 판단한다.
CREATE TABLE email_verifications (
    email_verification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    email VARCHAR(255) NOT NULL,
    code_hash VARCHAR(64) NOT NULL,
    sent_at DATETIME NOT NULL,
    failed_attempts INT NOT NULL DEFAULT 0,
    verified_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 회원의 가장 최근 발송 기록 조회(sent_at DESC), 오늘 발송 횟수 계산(sent_at >= ?)
CREATE INDEX idx_email_verifications_member_id_sent_at ON email_verifications (member_id, sent_at);
