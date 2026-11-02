-- 배너 카테고리. 운영진이 등록 화면에서 고르는 값이고, 배경 이미지가 이 값으로 정해진다.
--
-- ETC를 뺀 다섯 값은 카테고리마다 배경이 하나로 정해져 있어 운영진이 이미지를 고르지 않는다. 어느 파일이
-- 그 배경인지는 files의 행 id라 환경마다 달라서, 코드가 아니라 admin_config_values(V4)에
-- home.banner.preset.{카테고리} 키로 두고 등록·수정 때 서버가 꺼내 image_file_id에 채운다.
-- ETC만 운영진이 직접 올린 파일 id가 들어간다.
--
-- 프리셋 다섯 장을 files에 올리는 일과 그 id를 admin_config_values에 넣는 일은 값이 환경마다 달라
-- 이 파일이 하지 못한다. 배포할 때 손으로 넣는다(V32의 두 테이블과 같은 상황이다).
--
-- 쓰기 API가 생기기 전에 DB로 직접 넣은 행(V32 주석 참고)이 있을 수 있어 DEFAULT를 준다(3-3절).
ALTER TABLE home_banners
    ADD COLUMN category VARCHAR(20) NOT NULL DEFAULT 'GENERAL_NOTICE' AFTER subtitle;
