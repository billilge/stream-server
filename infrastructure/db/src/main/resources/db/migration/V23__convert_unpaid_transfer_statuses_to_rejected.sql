-- 이전 흐름에서는 운영진이 반려하면 상태를 UNPAID로 되돌렸다(처리 시각 reviewed_at은 남는다).
-- 이제 반려는 REJECTED이고, 한 번도 요청하지 않은 회원은 행이 없으므로 기존 UNPAID 행을 정리한다.
-- 처리된 적 있는 행은 반려된 요청이라 REJECTED로 바꾸고, 새로 반려할 때처럼 학생에게 보여줄 반려 사유를 채운다.
UPDATE student_transfer_statuses
SET status = 'REJECTED',
    reject_reason = '운영진이 납부를 확인하지 못했어요. 학생회에 문의해 주세요.'
WHERE status = 'UNPAID'
  AND reviewed_at IS NOT NULL;

-- 처리된 적 없는 UNPAID 행은 원래 생기지 않지만, 있다면 요청이 아니므로 지운다.
DELETE FROM student_transfer_statuses
WHERE status = 'UNPAID';
