# AGENTS.md

AI 코딩 에이전트가 이 저장소에서 작업할 때 참고하는 가이드다.

Java 21 + Spring Boot 4.1 + Spring Modulith 기반 단일 학생회 플랫폼(테넌트 구분 없음). DB는 MySQL.

## 작업 원칙

- 구현 전에 확실하지 않은 것은 반드시 사용자에게 질문한다.
- 요구사항이 여러 방식으로 해석될 수 있으면 임의로 선택하지 말고 선택지를 제시하고 물어본다.
- 컨벤션 문서에 없는 새로운 패턴을 도입해야 할 때, 어느 모듈/레이어에 둘지 애매할 때도 먼저 확인한다.
- 요청받은 범위만 수정한다. 인접 코드 개선·리팩토링은 임의로 하지 않는다.
- 테스트 코드는 지시하는 사람이 명시적으로 요청하지 않는 이상 작성하지 않는다.

## 모듈 구조

```
bootstrap                          # 최종 조립, verify() 테스트, 스케줄링
api/
├── common-api                     # ApiResponse, GlobalExceptionHandler 등 공통 응답/예외 처리
├── admin-api                      # 운영진 콘솔 (ADMIN)
└── app-api                        # 학생 앱 (STUDENT)
core/
├── common                         # ErrorCode/BusinessException, PrincipalProvider 등 공유 커널
└── domain/{도메인}                 # {Domain}Service(공개) / {Domain}InternalService(internal)
gateway/
├── auth                           # Spring Security, JWT
└── logging                        # MDC 필터, access log
infrastructure/
├── db                             # JPA Entity, RepositoryImpl, Flyway (MySQL)
├── client                         # 외부 API 클라이언트 구현
└── outbox                         # 아웃박스 릴레이 (폴러·재발행)
```

도메인 간 경계는 Gradle(컴파일 타임)로, 도메인 내부 공개/`internal` 경계는 `verify()`(CI)로 강제한다.

## 컨벤션 문서

작업 종류에 맞는 문서만 골라서 읽는다. 전체 인덱스와 "빠른 참조"는 `docs/conventions/00-index.md` 참고.

| 상황 | 문서 |
| --- | --- |
| 모듈 설계·의존성 | `architecture.md` |
| 코드 작성 (네이밍, DTO/Entity/Service 등) | `coding-style.md` |
| 에러 코드·예외 처리 | `error-handling.md` |
| 설정값·인증/인가 | `config-and-auth.md` |
| 테이블·인덱스 (Flyway) | `flyway-migration.md` |
| 커밋/브랜치/PR | `git-convention.md` |

## Git 컨벤션

자세한 내용은 `docs/conventions/git-convention.md` 참고.

- 커밋: `type: 제목` (한글, 마침표 없음). type은 `feat`/`fix`/`refactor`/`docs`/`test`/`chore`/`init`
- 작업 단위별로 커밋을 나눈다
- 브랜치: `{type}/#{이슈번호}-{작업내용}`
- PR 제목: `[{Type}/#{이슈번호}] {설명}`, Squash Merge 기본, `main` 직접 push 금지
