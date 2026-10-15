-- 납부 상태 행은 회원이 만들어질 때(첫 로그인) 함께 만든다. 그 전에 들어온 회원에게도 납부 전 행을 만들어 둔다.
-- 관리자 납부자 목록이 "납부 전" 회원까지 보여주려면 모든 회원에게 행이 있어야 한다.
-- 행이 없는 회원을 목록에 넣으려면 회원 기준으로 조회해야 하는데, 그러면 member 도메인이 회비를 알아야 한다.
INSERT INTO student_transfer_statuses (member_id, status)
SELECT m.member_id, 'UNPAID'
FROM members m
WHERE m.is_deleted = 0
  AND NOT EXISTS (
      SELECT 1
      FROM student_transfer_statuses s
      WHERE s.member_id = m.member_id
  );
