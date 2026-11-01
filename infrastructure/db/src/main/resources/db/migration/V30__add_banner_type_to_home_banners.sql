-- 배너 분류. 운영진 목록의 칩과 필터에만 쓴다. 지금 화면이 제휴와 그 밖을 나누므로 두 값이다.
--
-- 값은 운영진 화면이 정해 보낸다. 분류 칸이 이미지 선택 아래에 있고 제휴 프리셋을 고르면 PARTNERSHIP이
-- 자동으로 채워진다. 서버가 image_file_id로 프리셋을 알아볼 수는 없다 — 프리셋도 files의 한 행이고
-- 그 식별자는 환경마다 다르다. 그래서 분류가 이미지와 맞는지는 서버가 검증하지 않는다.
--
-- 쓰기 API가 생기기 전에 DB로 직접 넣은 행(V29 주석 참고)이 있을 수 있어 DEFAULT를 준다(3-3절).
ALTER TABLE home_banners
    ADD COLUMN banner_type VARCHAR(20) NOT NULL DEFAULT 'GENERAL' AFTER subtitle;
