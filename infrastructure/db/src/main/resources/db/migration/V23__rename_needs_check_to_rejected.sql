-- 확인필요 상태의 이름을 NEEDS_CHECK에서 REJECTED로 바꾼다. 화면 표기(확인필요)는 그대로다.
-- 반려된 학생이 다시 납부하는 흐름으로 바뀌므로(다음 PR) 상태 이름을 하는 일에 맞춘다.
UPDATE student_transfer_statuses
SET status = 'REJECTED'
WHERE status = 'NEEDS_CHECK';
