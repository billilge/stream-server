-- 미납부는 이제 행이 없는 상태다. 납부 정보 제출과 송금 확인 요청이 한 번으로 합쳐져 "제출만 한" 상태가 없어졌다.
-- UNPAID 행 중 운영진이 처리한 적 있는 행(reviewed_at이 있음)은 돌려보낸 요청이었으므로 REJECTED로 남기고,
-- 처리된 적 없는 행(V21이 전 회원에게 만든 행, 제출만 하고 확인 요청을 안 한 행)은 지운다.
UPDATE student_transfer_statuses
SET status = 'REJECTED'
WHERE status = 'UNPAID'
  AND reviewed_at IS NOT NULL;

DELETE FROM student_transfer_statuses
WHERE status = 'UNPAID';
