-- 답변을 학생에게 공개한 시각. NULL이면 아직 임시저장 단계라 학생 앱에 보이지 않는다.
-- 관리자 화면의 `임시저장`(PATCH .../answers)과 `등록하기`(POST .../answers/publish)를 가르는 값이다.
-- 지금까지 답변을 달 수 있는 API 자체가 없어 운영 데이터에 답변이 한 건도 없으므로, 기존 행은 NULL(비공개)이 맞다.
ALTER TABLE feedback_rounds
    ADD COLUMN answers_published_at DATETIME NULL AFTER closes_at;
