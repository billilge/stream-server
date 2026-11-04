-- 로그인마다 학번으로 미연결 명부 행을 찾는 조회가 돈다. 자주 쓰이니 인덱스를 둔다.
CREATE INDEX idx_payers_student_id ON payers (student_id);
