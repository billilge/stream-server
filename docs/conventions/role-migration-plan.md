# Role Migration Plan — 레거시 권한 흡수 계획 (미구현)

> **상태: 계획(Planning). 아직 구현/실행하지 않는다.** 실제 마이그레이션 파일은 이 계획이 확정된 뒤 `flyway-migration.md` 규칙에 따라 작성한다.
> 이 문서는 기존 시스템의 평면 role(`USER/ADMIN/WORKER/GA`)을 신규 2계층 권한 모델(`config-and-auth.md` 4절)로 흡수하기 위한 계획과 미결 사항만 정리한다.

---

## 1. 배경

- 흡수 대상 기존 시스템의 role enum: `USER`, `ADMIN`, `WORKER`, `GA`.
  - `USER`: 일반 학생
  - `ADMIN`: 관리자
  - `WORKER`: 총무부를 **제외한** 모든 부서의 담당자(범주)
  - `GA`: 총무부 담당자 (General Affairs)
- 신규 모델: 기본 role `STUDENT` / `ADMIN` + 관리자에게만 부여되는 부서(`Department`) authority(`DEPT_*`). 상세는 `config-and-auth.md`.

---

## 2. 매핑 계획

| 기존 role | 신규 role | 신규 부서(Department) | 비고 |
| --- | --- | --- | --- |
| `USER` | `STUDENT` | — | 1:1 단순 매핑 |
| `ADMIN` | `ADMIN` | (미정) | 부서 배정 정책 확정 필요(3-2) |
| `GA` | `ADMIN` | `GENERAL_AFFAIRS`(총무부) | 부서가 명확해 자동 매핑 가능 |
| `WORKER` | `ADMIN` | (개인별 부서 — 소스 데이터 필요) | 총무부 제외 전 부서라는 **범주**일 뿐, 개인별 실제 부서는 role 값만으로 알 수 없음(3-1) |

---

## 3. 미결 사항 (확정 후 진행)

### 3-1. `WORKER`의 개인별 부서 소스 데이터

`WORKER`를 `ADMIN + DEPT_{부서}`로 옮기려면 각 사용자가 실제로 어느 부서였는지가 필요하다. 이 정보가 기존 시스템 어디에 있는지 확인해야 한다.

- (A) 별도 컬럼/테이블에 부서 정보가 있음 → 그 값을 `user_departments`로 백필.
- (B) 부서 정보가 없음 → `WORKER`는 일괄 `ADMIN` 전환만 하고, 부서는 운영자가 콘솔에서 **수기 배정**. 배정 전까지 부서 단위 인가가 필요한 기능은 접근 불가(정상 동작).

> 현재는 (A)/(B) 미확정. 확정 시 이 절을 갱신하고 마이그레이션 방식을 결정한다.

### 3-2. `ADMIN`의 부서 배정 정책

기존 `ADMIN`이 특정 부서에 속하는지, 아니면 부서 무관 상위 관리자인지 미정. 후자라면 `DEPT_*` 없이 `ADMIN`만 부여하되, 부서 단위 인가가 필요한 기능에서의 취급(전체 허용 여부)을 정의해야 한다.

### 3-3. `Department` enum 최종 목록

`config-and-auth.md`의 부서 목록은 예시(`GENERAL_AFFAIRS`, `PLANNING`, `PR`, `WELFARE`)다. 소프트웨어융합대학 학생회의 실제 부서 목록을 확정해 enum과 매핑에 반영한다.

---

## 4. 마이그레이션 절차 초안 (확정 후 실제 SQL 작성)

아래는 방향 스케치이며, 3절이 확정된 뒤 `flyway-migration.md` 규칙에 맞춰 실제 파일(`V{n}__migrate_legacy_roles.sql` 등)로 작성한다.

1. 신규 스키마 준비 — `users.role`(STUDENT/ADMIN), `user_departments`는 이미 `flyway-migration.md` 4절에서 생성.
2. role 값 변환 — `USER → STUDENT`, 그 외(`ADMIN/WORKER/GA`) → `ADMIN`.
3. 부서 백필 — `GA → user_departments(GENERAL_AFFAIRS)`. `WORKER`는 3-1의 결정에 따라 (A) 소스 값 백필 또는 (B) 스킵(수기 배정).
4. 검증 — 변환 후 role 분포·부서 배정 건수 카운트로 데이터 정합성 확인. 되돌릴 수 있도록 원본 role 값을 백업 컬럼/테이블로 보존하는 것을 권장.

> 데이터 마이그레이션은 스키마 변경과 별도 파일로 분리한다(`flyway-migration.md` 3-5절). 대량 변경 시 실행 시간/락 영향을 검토한다.

---

## 5. 완료 조건

- 3-1 ~ 3-3 확정.
- 매핑표(2절)에 `WORKER`/`ADMIN` 부서가 확정 값으로 채워짐.
- 실제 마이그레이션 파일 작성 및 MySQL 기반 환경에서 검증 통과.

이 조건이 충족되면 이 문서는 "계획"에서 실제 마이그레이션 기록으로 승격하거나, `flyway-migration.md`로 통합한다.
