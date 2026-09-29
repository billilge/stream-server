-- 빌릴게 물품 목록에 카테고리 필터와 대여품 반납 정책을 추가한다.
--
-- category: 기존 행이 있을 수 있어 DEFAULT로 채운 뒤 DEFAULT를 제거한다(flyway-migration.md 3-3절).
--   DEFAULT를 남기면 INSERT가 category를 빠뜨려도 조용히 DAILY_SUPPLIES로 들어가므로, 이후엔 항상 명시하게 한다.
--   ⚠️ 기존 items 행의 카테고리는 실제 분류가 아니라 임시값이다. 물품 등록·수정 API가 아직 없어
--      운영진이 DB에서 직접 바로잡아야 한다. 적용 전 SELECT COUNT(*) FROM items 로 행 유무를 확인한다.
-- max_rental_days / return_deadline: 대여품(RENTAL)에만 있는 값이라 NULL을 허용한다.
--   대여일부터 최대 대여 가능 일수(0이면 당일 반납)와 반납 마감 시각(KST 기준 시각)이다.
--   ⚠️ 기존 RENTAL 행은 NULL로 남으므로 반납 마감이 계산되지 않는다. 마찬가지로 직접 채워야 한다.
ALTER TABLE items
    ADD COLUMN category VARCHAR(30) NOT NULL DEFAULT 'DAILY_SUPPLIES' AFTER name, -- ELECTRONICS / DAILY_SUPPLIES / MEDICINE / HYGIENE
    ADD COLUMN max_rental_days INT NULL AFTER image_key,
    ADD COLUMN return_deadline TIME NULL AFTER max_rental_days;

ALTER TABLE items
    ALTER COLUMN category DROP DEFAULT;

-- 물품 목록은 WHERE (category = ?) AND (name LIKE ?) ORDER BY name, id 로 조회한다.
-- category는 값이 4개뿐인 선택 필터라 앞에 두면 category 없는 조회에서 정렬을 못 받쳐 filesort가 생긴다(V6과 같은 이유).
-- 그래서 정렬 키인 name만 인덱스로 두고, InnoDB가 세컨더리 인덱스 끝에 PK(id)를 붙이므로 (name, id) 정렬이 커버된다.
-- keyword는 부분 일치(LIKE '%..%')라 인덱스를 못 타지만, 물품 수가 적어 name 인덱스 순서 스캔 + 필터로 충분하다.
CREATE INDEX idx_items_name ON items (name);

-- 내 대여 이력은 항상 WHERE member_id = ? [AND rental_status = ?] ORDER BY applied_at DESC, history_id DESC 로 조회한다.
-- 필터 컬럼을 앞, 정렬 컬럼을 뒤에 둔 복합 인덱스로 바꾼다(V7과 같은 방식).
-- 기존 idx_rental_histories_member_id는 새 인덱스의 좌측 prefix라 완전히 포함되므로 제거한다.
-- rental_status는 한 회원의 이력 안에서 거르는 조건이라 인덱스에 넣지 않는다(회원당 이력이 적다).
DROP INDEX idx_rental_histories_member_id ON rental_histories;

CREATE INDEX idx_rental_histories_member_id_applied_at
    ON rental_histories (member_id, applied_at);
