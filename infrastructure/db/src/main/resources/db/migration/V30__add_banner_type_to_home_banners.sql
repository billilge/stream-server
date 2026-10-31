-- 배너 분류. 운영진 목록의 칩과 필터에만 쓴다. 지금 화면이 제휴와 그 밖을 나누므로 두 값이다.
--
-- 이미지와는 무관하다 — 어떤 이미지를 쓸지는 image_file_id가 혼자 정하고, 프리셋도 직접 업로드와 같이
-- files에 올려 식별자로 가리킨다. 랜딩 타입과도 다른 축이라 둘 중 어느 쪽에서도 파생할 수 없다.
--
-- 쓰기 API가 생기기 전에 DB로 직접 넣은 행(V29 주석 참고)이 있을 수 있어 DEFAULT를 준다(3-3절).
ALTER TABLE home_banners
    ADD COLUMN banner_type VARCHAR(20) NOT NULL DEFAULT 'GENERAL' AFTER subtitle;
