-- 피드백 내용을 1000자까지 받도록 question 컬럼 길이를 늘린다.
ALTER TABLE open_feedbacks
    MODIFY COLUMN question VARCHAR(1000) NOT NULL;
