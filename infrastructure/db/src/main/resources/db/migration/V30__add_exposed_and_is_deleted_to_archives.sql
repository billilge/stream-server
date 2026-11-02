-- 관리자 API가 아카이빙을 목록에서 걸러내고 삭제할 수 있게 한다.
--
-- 노출 여부: 슬랑제(연말 행사) 전용 페이지에 이 아카이빙을 띄울지. 운영진이 목록에서 켜고 끈다.
-- 아카이빙이 노출될 곳은 그 페이지뿐이라 컬럼 이름에는 슬랑제를 넣지 않는다.
-- 삭제: 실수로 지운 아카이빙을 되살릴 수 있도록 행은 남긴다 (flyway-migration.md 3-4절).
-- 운영 중인 행이 있어 둘 다 DEFAULT를 준다(3-3절). 기존 아카이빙은 "노출 안 함 · 살아 있음"으로 시작한다.
--
-- 인덱스는 두지 않는다. 아카이빙은 연간 수십 건 규모라 목록이 테이블을 통째로 읽어도 되고,
-- 제목 검색이 LIKE '%...%'라 어차피 인덱스를 타지 못한다.
ALTER TABLE archives
    ADD COLUMN is_exposed TINYINT(1) NOT NULL DEFAULT 0,
    ADD COLUMN is_deleted TINYINT(1) NOT NULL DEFAULT 0;
