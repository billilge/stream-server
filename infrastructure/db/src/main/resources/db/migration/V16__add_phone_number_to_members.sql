-- 회원가입 때 받는 휴대전화 번호. 하이픈 없이 숫자만 저장한다(01012345678).
-- 가입 전 회원과 기존(이관) 회원은 값이 없으므로 NULL을 허용한다.
ALTER TABLE members
    ADD COLUMN phone_number VARCHAR(20) NULL AFTER email;

-- 활성 회원의 전화번호만 유니크 (탈퇴 후 같은 번호로 재가입 허용)
ALTER TABLE members
    ADD COLUMN active_phone_number VARCHAR(20)
        GENERATED ALWAYS AS (IF(is_deleted = 0, phone_number, NULL)) VIRTUAL;
CREATE UNIQUE INDEX uk_members_active_phone_number ON members (active_phone_number);
