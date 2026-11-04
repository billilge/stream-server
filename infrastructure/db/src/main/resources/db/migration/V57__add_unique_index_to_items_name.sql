-- 물품 이름 중복은 지금까지 애플리케이션 검증(existsByName/existsByNameAndIdNot)만 막고 있어서
-- 동시에 같은 이름으로 등록하면 둘 다 통과해 중복이 생길 수 있다. DB 유니크로 최종 방어선을 둔다.
-- 새 인덱스를 먼저 만들고 기존 인덱스를 지워서 그사이 인덱스가 비는 구간이 없게 한다.
CREATE UNIQUE INDEX uk_items_name ON items (name);

DROP INDEX idx_items_name ON items;
