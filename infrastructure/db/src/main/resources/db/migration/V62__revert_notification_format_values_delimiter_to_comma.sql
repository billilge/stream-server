-- 알림 문구 값(format_values)의 구분자를 레거시(빌릴게)와 같은 쉼표로 되돌린다.
-- 지금 서버가 만든 행과 V61이 바꾼 레거시 운영진 알림은 단위 구분 문자(US, CHAR(31))로 이어져 있어, DB를 직접 볼 때 값 사이가
-- 보이지 않는다. 앞으로는 쉼표로 저장하므로 US가 들어간 행을 모두 쉼표로 바꾼다. 다시 적용돼도 결과가 같다.
-- 값 안의 쉼표(행사명·물품명)는 항상 마지막 값이라, 읽을 때 문구에 들어가는 값 개수만큼만 나눠 그대로 남는다.
UPDATE notifications
SET format_values = REPLACE(format_values, CHAR(31), ',')
WHERE INSTR(format_values, CHAR(31)) > 0;
