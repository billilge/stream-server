-- payers, member_notification_settings, member_term_agreements는 도메인 객체·JPA Entity만 있고
-- Repository/Service/Controller 등 실제로 쓰는 코드가 없었고, ERD에도 없어 제거한다.
DROP TABLE payers;
DROP TABLE member_notification_settings;
DROP TABLE member_term_agreements;
