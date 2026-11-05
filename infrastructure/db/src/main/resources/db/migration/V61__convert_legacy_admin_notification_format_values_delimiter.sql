-- 레거시(빌릴게)에서 이관한 운영진 공용 알림(ADMIN_*)의 format_values 구분자를 쉼표에서 단위 구분 문자(US, CHAR(31))로 바꾼다.
-- 레거시는 값 목록을 쉼표로 이어 붙여 저장했지만, 지금 서버는 US로 나눠 읽는다. 값이 여러 개인 ADMIN_* 행은 값 하나로
-- 읽혀 문구를 만들다 실패하고, 운영진 알림 목록 조회가 500이 된다. 학생 알림은 값이 하나라 구분자와 상관없이 읽힌다.
-- 지금 서버가 만든 행은 이미 US를 쓰므로 US가 없는 행만 바꾼다. 다시 적용돼도 결과가 같다.
UPDATE notifications
SET format_values = REPLACE(format_values, ',', CHAR(31))
WHERE type IN ('ADMIN_RENTAL_APPLY', 'ADMIN_RENTAL_CANCEL', 'ADMIN_RETURN_APPLY')
  AND format_values IS NOT NULL
  AND INSTR(format_values, CHAR(31)) = 0;
