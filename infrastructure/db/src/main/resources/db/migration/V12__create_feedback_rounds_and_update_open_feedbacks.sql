CREATE TABLE feedback_rounds (
    feedback_round_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    year INT NOT NULL,
    round INT NOT NULL,
    opens_at DATETIME NOT NULL,
    closes_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_feedback_rounds_year_round (year, round)
);

-- open_feedbacks(V3)는 이미 머지된 파일이라 직접 수정하지 않고 여기서 이어서 변경한다.
-- 회차가 연도별로 1차부터 다시 시작하도록 year 컬럼을 추가하고, 화면에 카테고리 선택 UI가 없어 category는 제거한다.
-- questioned_at은 BaseTimeEntity의 created_at(DEFAULT CURRENT_TIMESTAMP, insertable=false) 대신 애플리케이션이 직접 채우는
-- 전용 컬럼이다 — 등록 응답에 질문 시각을 그 자리에서 바로 내려줘야 하기 때문이다.
ALTER TABLE open_feedbacks
    ADD COLUMN year INT NOT NULL AFTER round,
    ADD COLUMN questioned_at DATETIME NOT NULL AFTER question,
    MODIFY COLUMN question VARCHAR(500) NOT NULL,
    DROP COLUMN category;

CREATE INDEX idx_open_feedbacks_year_round ON open_feedbacks (year, round);
