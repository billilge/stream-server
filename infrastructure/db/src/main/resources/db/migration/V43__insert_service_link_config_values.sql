-- 학생 앱 설정 화면의 '의견 보내기'(구글폼)와 '개인정보 처리방침'(노션) 링크. 키는 ServiceLinkConfigKeys와 같다.
-- 의견 보내기 링크는 테스트용이다. 실제 링크가 정해지면 새 마이그레이션에서 UPDATE로 바꾼다.
INSERT INTO admin_config_values (config_key, config_value)
VALUES ('SERVICE_FEEDBACK_FORM_URL',
        'https://docs.google.com/forms/d/1mNHqDw4Rj86etsGK1rag0neTjLwy68JfSGLY3dsu4B8/'),
       ('SERVICE_PRIVACY_POLICY_URL',
        'https://almond-look-5ec.notion.site/Stream-40eef1478680803dbd28d7a7923b78d3');
