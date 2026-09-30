-- 로그인 provider가 주는 학적 상태 원문(재학·휴학·졸업 등). 로그인할 때마다 갱신한다.
-- 로그인 전인 기존(이관) 회원은 값이 없으므로 NULL을 허용한다.
ALTER TABLE members
    ADD COLUMN academic_status VARCHAR(50) NULL AFTER department;
