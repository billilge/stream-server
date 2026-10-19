-- 학생에게 안내한 납부 금액을 요청 시점 값으로 남긴다(남은 학기 수 × 그때의 학기당 회비).
-- 운영진이 학기당 회비를 바꿔도 이미 들어온 요청의 금액은 바뀌지 않게 하려는 것이다.
-- 이 컬럼이 생기기 전의 행은 금액을 모르므로 NULL을 허용한다. 계산 결과가 long이라 BIGINT로 둔다(ddl-auto validate).
ALTER TABLE student_transfer_statuses
    ADD COLUMN amount BIGINT NULL AFTER remaining_semesters;
