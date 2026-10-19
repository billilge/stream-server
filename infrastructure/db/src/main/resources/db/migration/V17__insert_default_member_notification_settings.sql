-- 알림 설정은 회원가입할 때 만든다. 그 전에 들어온 회원에게도 기본 설정(모두 켜짐)을 만들어 둔다.
-- 아직 가입을 마치지 않은 회원도 포함한다. 나중에 가입하면 이미 있는 설정을 그대로 쓴다.
INSERT INTO member_notification_settings (member_id)
SELECT m.member_id
FROM members m
WHERE m.is_deleted = 0
  AND NOT EXISTS (
      SELECT 1
      FROM member_notification_settings s
      WHERE s.member_id = m.member_id
  );
