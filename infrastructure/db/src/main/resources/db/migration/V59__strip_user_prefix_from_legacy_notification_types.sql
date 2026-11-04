-- 레거시(빌릴게)에서 이관한 notifications.type 값 중 회원 알림(USER_*)의 접두사를 뗀다.
-- NotificationType enum 상수 이름이 이 컬럼에 그대로 저장되는데, 다른 상수는 모두 접두사가 없어서
-- (RENTAL_APPLY 등) 컨벤션에 맞춰 USER_만 지운다. 운영진 공용 알림(ADMIN_*)은 그대로 둔다.
-- 운영 DB는 이관 SQL로 이미 바뀌어 있어 다시 적용돼도 결과가 같다.
UPDATE notifications
SET type = REPLACE(type, 'USER_', '')
WHERE type LIKE 'USER_%';
