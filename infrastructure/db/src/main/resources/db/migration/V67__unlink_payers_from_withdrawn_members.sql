-- 이미 탈퇴(소프트 삭제)한 회원에 연결된 납부자 명부 행의 연결을 끊는다.
-- 탈퇴할 때 명부 연결을 끊지 않아, 같은 학번으로 다시 가입해도 로그인 때 연결할 미연결 행(member_id IS NULL)을 찾지 못해
-- 납부자로 인정되지 않았다. 앞으로는 탈퇴할 때 끊으므로, 그전에 탈퇴한 회원의 행만 가입 전 상태로 되돌린다.
-- 다시 적용돼도 결과가 같다.
UPDATE payers p
JOIN members m ON m.member_id = p.member_id
SET p.member_id = NULL,
    p.registered = 0
WHERE m.is_deleted = 1;
