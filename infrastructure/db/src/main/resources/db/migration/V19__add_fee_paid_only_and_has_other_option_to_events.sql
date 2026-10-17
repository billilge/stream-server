-- 학생회비 납부자만 신청할 수 있는 행사인지
ALTER TABLE events
    ADD COLUMN fee_paid_only TINYINT(1) NOT NULL DEFAULT 0;

-- 선택형 질문에 '기타'(직접 입력) 선택지를 덧붙이는지
ALTER TABLE event_questions
    ADD COLUMN has_other_option TINYINT(1) NOT NULL DEFAULT 0;
