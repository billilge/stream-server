-- 레거시(빌릴게) 데이터 이관으로 회원과 연결되지 않는 행이 생겨 member_id에 NULL을 허용한다.
--   payers: 앱에 가입하지 않은 납부 명부 인원. 가입 전이라 연결할 회원이 없다.
--   notifications: 특정 회원이 아니라 운영진 전체가 보던 관리자 공용 알림(ADMIN_*).
-- 운영 DB는 이관 SQL로 이미 바뀌어 있어 다시 적용돼도 결과가 같다.
--
-- 인덱스는 그대로 둔다. uk_payers_member_id는 MySQL에서 NULL끼리 충돌하지 않고,
-- 조회는 항상 member_id = ? 라서 NULL 행은 회원 기준 조회에 섞이지 않는다.
ALTER TABLE payers
    MODIFY member_id BIGINT NULL;

ALTER TABLE notifications
    MODIFY member_id BIGINT NULL;
