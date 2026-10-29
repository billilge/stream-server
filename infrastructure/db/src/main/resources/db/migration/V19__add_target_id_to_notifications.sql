-- 알림함에서 알림을 눌렀을 때 이동할 대상의 ID. 무엇의 ID인지(행사·사물함 회차·공지·대여 이력)는 type이 정한다.
-- 대상이 없는 알림도 생길 수 있어 NULL을 허용한다.
ALTER TABLE notifications
    ADD COLUMN target_id BIGINT NULL AFTER format_values;
