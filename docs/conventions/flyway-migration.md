# Flyway Migration — DB 마이그레이션 컨벤션 (MySQL 8.x)

> Flyway 마이그레이션 파일의 위치·네이밍·작성 규칙, soft delete 인덱스 처리, 아웃박스/사용자 스키마를 다룬다.
> 테이블/컬럼 추가·변경, 새 도메인 스키마 작업 시 참조한다.
> 레거시 role(`USER/ADMIN/WORKER/GA`) 흡수 마이그레이션은 `role-migration-plan.md`(계획, 미구현) 참조.

---

## 1. 위치

- 마이그레이션 파일은 `infrastructure:db`에 둔다(JPA Entity와 동일 모듈). 아웃박스 테이블 스키마도 여기서 함께 관리한다(소유 일원화).

```
infrastructure/db/src/main/resources/db/migration/
├── V1__create_user_tables.sql
├── V2__create_outbox_table.sql
├── V3__create_rental_table.sql
└── ...
```

---

## 2. 파일 네이밍

- Flyway 기본 규칙: `V{버전}__{설명}.sql` (버전은 순차 증가 정수).
- 설명은 영문 snake_case, 변경 내용을 동사로 시작(`create_{table}_table`, `add_{column}_to_{table}`, `add_index_to_{table}` 등).
- **버전 충돌 주의**: 여러 브랜치 동시 작업 시 머지 직전 번호 중복을 확인하고, 겹치면 머지하는 쪽에서 재조정한다.

---

## 3. 작성 규칙

### 3-1. 불변성

- 이미 머지되어 적용된 파일은 **수정하지 않는다.** 변경은 새 버전 파일로 추가한다.

### 3-2. 테이블 생성

- 테이블명은 복수형 snake_case(`members`, `rentals`, `lockers`).
- 기본 컬럼(`id`, `created_at`, `updated_at`) 포함. 소프트 삭제 대상은 `deleted_at`도 포함.
- 다른 테이블 참조 컬럼(`member_id` 등)에도 `REFERENCES`/`FOREIGN KEY`를 걸지 않는다 — 참조 무결성은 애플리케이션 레벨에서 관리하고 DB는 컬럼·인덱스만 둔다(`coding-style.md` 2-5절).

```sql
-- V3__create_rental_table.sql
CREATE TABLE rentals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    return_date DATE NOT NULL,
    deleted_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 3-3. 컬럼 추가/변경

- 운영 데이터가 있는 테이블에 `NOT NULL` 컬럼을 추가할 때는 `DEFAULT`를 지정한다.

```sql
ALTER TABLE rentals
    ADD COLUMN penalty_point INT NOT NULL DEFAULT 0;
```

### 3-4. 인덱스 (soft delete 포함)

- 조회 빈도가 높은 컬럼, WHERE/ORDER BY에 자주 쓰이는 컬럼에 인덱스를 추가한다. 인덱스명은 `idx_{table}_{column...}`, 유니크는 `uk_{table}_{column...}`.

**soft delete 인덱싱 규칙 (MySQL)** — MySQL은 Postgres 같은 부분 인덱스(`WHERE deleted_at IS NULL`)가 없으므로 아래를 따른다.

1. **`deleted_at` 단독 인덱스를 만들지 않는다.** 값이 대부분 NULL이라 카디널리티가 낮아 옵티마이저가 거의 쓰지 않고 쓰기 비용만 는다.
2. **실제 필터 컬럼을 앞, `deleted_at`을 뒤(trailing)에 둔 복합 인덱스**를 만든다. 실제 조회는 항상 `WHERE {filter} = ? AND deleted_at IS NULL` 형태이고, `IS NULL`은 옵티마이저가 equality처럼 다룬다.

```sql
-- WHERE member_id = ? AND deleted_at IS NULL
CREATE INDEX idx_rentals_member_id_deleted_at ON rentals (member_id, deleted_at);

-- 정렬까지 커버 (WHERE member_id = ? AND deleted_at IS NULL ORDER BY created_at DESC)
CREATE INDEX idx_rentals_member_id_deleted_at_created_at
    ON rentals (member_id, deleted_at, created_at);
```

> InnoDB는 세컨더리 인덱스 끝에 PK(`id`)를 자동으로 붙이므로, `id` 기준 커서 페이지네이션은 `(member_id, deleted_at)`만으로도 정렬이 유지된다. 다른 정렬이 필요할 때만 그 컬럼을 뒤에 추가한다.

3. **유니크 제약이 필요한 컬럼은 generated column으로 "활성 행에만" 건다.** `UNIQUE(col)`은 삭제된 행이 값을 점유해 재사용을 막고, `UNIQUE(col, deleted_at)`은 **활성 중복이 뚫린다**(MySQL은 유니크 인덱스에서 NULL을 서로 다른 값으로 취급하므로 `(col, NULL)`이 여러 개 허용됨). 활성 행이면 값, 삭제 행이면 NULL을 갖는 가상 컬럼에 유니크를 건다.

```sql
-- 활성 학번만 유니크 (삭제 후 재등록 허용)
ALTER TABLE members
    ADD COLUMN active_student_no VARCHAR(20)
        GENERATED ALWAYS AS (IF(deleted_at IS NULL, student_no, NULL)) VIRTUAL;

CREATE UNIQUE INDEX uk_members_active_student_no ON members (active_student_no);
```

> `VIRTUAL`이라 테이블 저장 공간을 쓰지 않고 인덱스에만 실체화된다. MySQL엔 부분 인덱스가 없어 이 패턴으로 대체한다.

### 3-5. 데이터 마이그레이션

- 스키마 변경과 데이터 마이그레이션은 가능하면 별도 파일로 분리한다. 대량 변경은 실행 시간/락 영향을 별도 검토한다.

---

## 4. 사용자 & 부서 스키마

권한 모델(`config-and-auth.md` 4절)에 맞춘 스키마다. 테넌트 식별자(매장/학생회 ID)는 없다.

```sql
-- V1__create_user_tables.sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL,          -- STUDENT / ADMIN
    deleted_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 활성 계정만 login_id 유니크 (soft delete 재사용 대비, 3-4절)
ALTER TABLE users
    ADD COLUMN active_login_id VARCHAR(50)
        GENERATED ALWAYS AS (IF(deleted_at IS NULL, login_id, NULL)) VIRTUAL;
CREATE UNIQUE INDEX uk_users_active_login_id ON users (active_login_id);

-- 관리자-부서 다대다 (ADMIN 계정만 행이 존재)
CREATE TABLE user_departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    department VARCHAR(30) NOT NULL,    -- Department.name()
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE UNIQUE INDEX uk_user_departments_user_department ON user_departments (user_id, department);
CREATE INDEX idx_user_departments_user_id ON user_departments (user_id);
```

> 기존 시스템의 `USER/ADMIN/WORKER/GA` 값을 위 모델로 옮기는 마이그레이션은 `role-migration-plan.md`에 계획으로만 정리한다(현재 미구현).

---

## 5. 아웃박스 테이블

`infrastructure:outbox` 릴레이가 사용하는 공용 테이블(`architecture.md` 6-2절). 특정 도메인에 종속되지 않는다.

```sql
-- V2__create_outbox_table.sql
CREATE TABLE outbox_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(200) NOT NULL,
    payload JSON NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',   -- PENDING / PUBLISHED
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at DATETIME NULL
);
CREATE INDEX idx_outbox_events_status_id ON outbox_events (status, id);
```

- 발행 도메인은 자기 트랜잭션 안에서 `OutboxWriter`로 기록(원 비즈니스 쓰기와 원자적).
- 릴레이(`@Scheduled` 폴러)는 `status = 'PENDING'`을 조회해 인프로세스 재발행 후 `PUBLISHED`로 마킹한다. at-least-once이므로 소비 리스너는 멱등해야 한다.

---

## 6. JPA Entity와의 관계 & 프로파일

- JPA Entity 필드 변경은 반드시 대응 마이그레이션과 함께 작성한다(`coding-style.md` 2-5절).
- `prod`는 `ddl-auto: validate`(또는 `none`) + Flyway로만 스키마 관리.

```yaml
# application-infrastructure-db.yml — prod
spring:
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
    locations: classpath:db/migration
```

- **local/test**: 마이그레이션이 MySQL 전용 문법(`GENERATED ALWAYS ... VIRTUAL`, `ON UPDATE CURRENT_TIMESTAMP` 등)을 쓰므로 H2에서 그대로 실행되지 않는다. 스키마 정합성 검증은 실제 MySQL을 쓰는 환경(Testcontainers MySQL 등)에서 수행하는 것을 원칙으로 하고, 순수 로컬 편의 목적이면 `spring.flyway.enabled: false` + `ddl-auto: create-drop`으로 대체하되 정합성은 MySQL 기반 CI에 맡긴다. 테스트는 `clean` 후 전체 마이그레이션 재실행으로 drift를 방지한다.
