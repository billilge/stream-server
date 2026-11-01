-- 회원가입 때 인증한 이메일. 이관 회원의 email은 이전 시스템에서 인증한 값이라 그대로 둔다.
-- 활성 회원의 이메일만 유니크 (탈퇴 후 같은 이메일로 재가입 허용)
ALTER TABLE members
    ADD COLUMN active_email VARCHAR(255)
        GENERATED ALWAYS AS (IF(is_deleted = 0, email, NULL)) VIRTUAL;
CREATE UNIQUE INDEX uk_members_active_email ON members (active_email);
