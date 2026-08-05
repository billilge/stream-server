# 소프트웨어융합대학 학생회 플랫폼 — 컨벤션 인덱스

Java 21 + Spring Boot 4.1 + Spring Modulith 기반, 단일 학생회 플랫폼의 개발 컨벤션을 용도별 문서로 분리했다. 작업 종류에 맞는 문서만 읽으면 된다.

**핵심 전제**
- 학생이 **대여·행사 신청·사물함 신청·회비 납부** 등을, 운영진(부서별 관리자)이 관리를 수행하는 **단일 학생회** 플랫폼(테넌트 식별자 없음).
- 언어 Java 21, DB MySQL 8.x. Spring Modulith는 경계 검증 `verify()` 전용.
- 도메인 간 경계는 Gradle(컴파일 타임), 도메인 내부(공개/`internal`) 경계는 `verify()`(CI 타임)로 강제.
- 교차 도메인 동기 조합은 api의 `UseCase`, 비동기 반응은 이벤트 + 직접 구현 아웃박스. Implement Layer는 두지 않는다.
- 권한은 2계층: 기본 role(`STUDENT`/`ADMIN`) + 관리자 부서(`Department`, `DEPT_*`).

| 문서 | 다루는 내용 | 언제 참조하는가 |
| --- | --- | --- |
| [`architecture.md`](./architecture.md) | 모듈 구조, 의존 방향, Modulith 경계 규칙, 레이어, 도메인 간 통신(UseCase·이벤트·아웃박스) | 새 모듈/도메인 설계, 의존성 리뷰 |
| [`coding-style.md`](./coding-style.md) | 네이밍, 도메인 객체(record)/DTO/Command/Entity/Repository/Service/UseCase 패턴, Validation | 실제 코드 작성/리뷰 |
| [`error-handling.md`](./error-handling.md) | `ErrorCode`/`BusinessException`, `GlobalExceptionHandler`, `@ApiErrorCode` Swagger 문서화 | 에러 코드 추가, 예외 처리 |
| [`config-and-auth.md`](./config-and-auth.md) | 설정 바인딩, 2계층 권한 모델(role + 부서), `PrincipalProvider`, `DepartmentAccessChecker` | 설정값 추가, 인증·인가 작업 |
| [`logging.md`](./logging.md) | MDC 요청 추적, `MdcFilter`/`LoggingFilter`, 로그 레벨, JSON 로깅 | 로깅 코드, MDC 필드 추가 |
| [`flyway-migration.md`](./flyway-migration.md) | Flyway 위치/네이밍, 테이블·인덱스 규칙, **soft delete 인덱스(MySQL)**, 사용자/부서·아웃박스 스키마 | 테이블/컬럼·인덱스, 스키마 작업 |
| [`git-convention.md`](./git-convention.md) | 커밋/브랜치/PR/이슈 규칙, CI 필수 체크(`verify()`) | 커밋, 브랜치, PR |
| [`role-migration-plan.md`](./role-migration-plan.md) | 레거시 `USER/ADMIN/WORKER/GA` 흡수 **계획(미구현)** 및 미결 사항 | 레거시 권한 마이그레이션 논의 시 |

## 빠른 참조

- "새 도메인(예: 사물함) 추가" → `architecture.md`(모듈/경계) → `coding-style.md`(클래스) → `flyway-migration.md`(테이블)
- "여러 도메인을 묶는 화면/처리" → `architecture.md` 6-1절 + `coding-style.md` 2-9절 (api의 UseCase)
- "행사 유료 신청처럼 원자적 다중 도메인 쓰기" → `architecture.md` 6-1절 (UseCase + `@Transactional`)
- "A 도메인 변화에 B가 반응" → `architecture.md` 6-2절 (이벤트 + 아웃박스)
- "부서 권한으로 승인 제한" → `config-and-auth.md` 4-4절 (`DepartmentAccessChecker`)
- "soft delete 컬럼 인덱스/유니크" → `flyway-migration.md` 3-4절
- "에러 코드 추가" → `error-handling.md`
- "도메인 내부 구현 숨기기" → `architecture.md` 4-3절 (최상위 공개 / `internal`)

## 모듈 한눈에 보기

```
bootstrap                       # @Modulithic, 최종 조립, verify() 테스트, 스케줄링
api/
├── common-api                  # ApiResponse, GlobalExceptionHandler, WebMvcConfig, ApiUser, @ApiErrorCode
├── admin-api                   # 운영진 콘솔 (ADMIN, /v1/admin/**)
└── app-api                     # 학생 앱 (STUDENT, /v1/app/**)
core/
├── common                      # ErrorCode/BusinessException/ErrorStatus, PrincipalProvider/Role/Department,
│                               # PageResult/CursorSliceResult, OutboxWriter, common.event (shared module)
└── domain/{member,rental,event,locker,fee,notice}
                                # {Domain}Service(공개)/{Domain}InternalService(internal)/
                                # {Domain}Repository·{Domain}Client(공개)/{Domain}ErrorCode
gateway/
├── auth                        # Spring Security, JWT, DepartmentAccessChecker
└── logging                     # MDC 필터, access log
infrastructure/
├── db                          # JPA Entity, RepositoryImpl, Flyway (MySQL)
├── client                      # 외부 API 클라이언트 구현
└── outbox                      # 아웃박스 릴레이 (폴러·재발행)
```
