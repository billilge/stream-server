# Architecture — 모듈 구조 & 레이어 규칙

> 소프트웨어융합대학 학생회 플랫폼(단일 학생회)의 모듈 구성, 모듈 간 의존 방향, 도메인 내부 경계, 레이어 규칙, 도메인 간 통신을 다룬다.
> 학생은 대여·행사 신청·사물함 신청·회비 납부 등을, 운영진(부서별 관리자)은 관리를 수행한다.
> 설계/리뷰, 새 모듈·도메인 추가 시 참조한다. 클래스 작성은 `coding-style.md`, 예외는 `error-handling.md`, 인증/권한은 `config-and-auth.md`, DB는 `flyway-migration.md`를 함께 본다.
> `{basePackage}`는 프로젝트 루트 패키지, `{도메인}`은 도메인 이름(예: `member`, `event`, `welfare`)의 자리표시자다.

---

## 1. 기술 스택 & 베이스라인

| 항목 | 버전/기준 |
| --- | --- |
| Java | 21 (LTS) |
| Spring Boot | 4.1.0 |
| Spring Framework | 7.0.8+ |
| Spring Modulith | 2.1.0 (경계 검증 `verify()` 용도로만 사용) |
| DB | MySQL 8.x |
| 플랫폼 | Jakarta EE 11 / Servlet 6.1 / Jackson 3 / JUnit 6 |

- Spring Boot 4.1.0은 최소 Java 17을 요구하며 Java 26까지 호환되고 Spring Framework 7.0.8 이상을 필요로 한다. Java 21은 가상 스레드 등 JVM 기능을 위해 권장되는 버전이다.
- 버전은 루트 `gradle/libs.versions.toml`(버전 카탈로그)에서 단일 관리한다. 모듈별 `build.gradle.kts`에 버전을 하드코딩하지 않는다.
- Spring Boot 플러그인과 `bootJar`는 **`bootstrap` 모듈에서만** 활성화한다. 나머지는 라이브러리로 두고 Boot BOM을 `platform`으로 가져온다.
- Spring Modulith는 경계 검증(`verify()`) 목적으로만 쓰며, `spring-modulith-core`는 **`bootstrap`의 test scope에만** 둔다. 도메인 모듈은 `@ApplicationModule(type = OPEN)` 선언을 위해 어노테이션 전용 `spring-modulith-api`만 `compileOnly`로 갖는다(4-3절). 이벤트 처리에는 Modulith를 쓰지 않는다(6-2절).

---

## 2. 모듈 구조

```
root
├── bootstrap/            # 실행 가능한 애플리케이션 (@Modulithic, 최종 조립, 스케줄링 활성화)
├── api/                  # Presentation 레이어 그룹 (Controller, Request/Response, UseCase)
│   ├── common-api/       # role 무관 공통 인프라 (ApiResponse, GlobalExceptionHandler, WebMvcConfig, ApiUser)
│   ├── admin-api/        # 운영진 콘솔 (ADMIN, /v1/admin/**)
│   └── app-api/          # 학생 앱 (STUDENT, /v1/app/**)
├── core/                 # (빈 컨테이너 — 코드 없음)
│   ├── common/           # 공유 커널 (순수 Java). verify 설정에서 shared module로 선언
│   │   ├── (common)      #   ApiResponse 규격, ErrorCode/CommonErrorCode/BusinessException, ErrorStatus,
│   │   │                 #   PageResult/CursorSliceResult, PrincipalProvider·Role·Department, RegexPatterns,
│   │   │                 #   OutboxWriter(아웃박스 쓰기 포트)
│   │   └── (common.event)#   크로스 도메인 이벤트 타입 (DomainEvent 마커 + 구체 이벤트)
│   └── domain/           # (빈 컨테이너 — 코드 없음)
│       ├── auth/         #   인증 (로그인·토큰 발급 흐름)
│       ├── member/       #   학생 회원
│       ├── event/        #   행사 신청·사물함 신청·아카이빙 (행사·사물함은 각각 자체 신청 프로세스)
│       ├── welfare/      #   물품 대여·회비·공지 (대여는 자체 신청 프로세스)
│       └── internal/     #   학생회 내부 운영 (운영진·부서 관리 등 어드민 전용)
├── gateway/              # 횡단관심사 그룹
│   ├── auth/             # 인증/인가 (SecurityConfig(role→URL), JWT, DepartmentAccessChecker). config-and-auth.md 참조
│   └── logging/          # MDC 기반 요청 추적. logging.md 참조
└── infrastructure/       # 기술 구현 (아웃바운드 어댑터)
    ├── db/               # JPA Entity, Repository 구현체, Flyway 마이그레이션 (MySQL)
    ├── client/           # 외부 API 클라이언트 구현체
    └── outbox/           # 아웃박스 릴레이 (테이블·폴러·재발행). 특정 도메인을 모름
```

- `core`·`core:domain`은 **코드 없는 빈 컨테이너**다. 실제 코드는 리프 모듈에만 둔다.
- 도메인은 팀/기능 단위로 `core:domain:{도메인}` 리프 모듈로 추가한다. 도메인 모듈끼리는 Gradle 의존을 선언하지 않는다.
- 대여(`welfare`)·행사·사물함(`event`)은 각각 **자체 신청 프로세스**를 갖는다. 공통 `application` 도메인으로 묶지 않는다 — 신청 흐름이 기능마다 달라 중복을 감수하고 경계를 지키는 쪽을 택한다.
- `infrastructure:outbox`는 여러 도메인이 공유하는 아웃박스 릴레이만 담고, 특정 도메인 타입을 알지 않는다(`core:common.event`의 이벤트 타입만 참조).

### 2-1. 모듈 그룹별 책임 & 경계

다섯 그룹은 **"무엇이 바뀌면 이 모듈이 바뀌는가(변경 축)"**가 서로 다르다는 기준으로 나눈다. 축이 같은 코드는 한 그룹에, 다른 코드는 다른 그룹에 둔다.

| 그룹 | 변경 축 | 담는 것 | 담지 않는 것 |
| --- | --- | --- | --- |
| `bootstrap` | 조립·기동 방식 | `@Modulithic` 메인, 최종 빈 조립, 스케줄링 활성화, 실행 설정, `bootJar` | 비즈니스 로직, 도메인 규칙 |
| `api:*` | 클라이언트 요구(요청/응답 형태) | Controller, Request/Response DTO, 교차 도메인 `UseCase` | 비즈니스 규칙, 영속화, 보안 정책 구현 |
| `core:common` | 공유 커널 규격 | 응답/에러 규격, 인증 추상(Principal·Role·Department), 페이지/커서 결과, 아웃박스 포트, 크로스 도메인 이벤트 타입 | 특정 도메인 개념(`{Domain}Id` 등), Spring·JPA·web·security |
| `core:domain:{도메인}` | 해당 도메인 규칙 | `{Domain}Service`(진입점)·도메인 객체(record)·아웃바운드 포트 인터페이스와 그 구현 로직 | 다른 도메인, web·security·JPA·Modulith |
| `gateway:*` | 횡단관심사 정책 | 인증/인가(`auth`), 요청 추적·access log(`logging`) | 도메인 규칙, 영속화 |
| `infrastructure:*` | 외부 기술(구현 세부) | `{Domain}Repository`/`{Domain}Client` 구현, JPA Entity·Flyway(`db`), 외부 API 어댑터(`client`), 아웃박스 릴레이(`outbox`) | 비즈니스 규칙, 도메인 진입점 |

- **의존 방향이 곧 책임 경계**다. 화살표는 항상 안쪽(`core`)을 향하고 `core`는 바깥(api·gateway·infrastructure)을 모른다(3절). 그래서 도메인 규칙은 기술·클라이언트가 바뀌어도 그대로다.
- **인터페이스는 소유자(`core:domain`)에, 구현은 기술 소유자(`infrastructure:*`)에** 둔다. `{Domain}Repository`/`{Domain}Client`는 도메인이 정의하고 인프라가 구현하는 아웃바운드 포트다(4-3절, 5절).
- **`core`·`core:domain`은 코드 없는 빈 컨테이너**다(59줄). 실제 코드는 리프 모듈(`core:common`, `core:domain:{도메인}`)에만 둔다. 그룹 노드는 Gradle 경계를 긋기 위한 뼈대일 뿐이다.
- 그룹별 허용 의존성 전체 목록은 7절 표에서 확인한다.

### 2-2. 프레젠테이션 레이어 분리 축 — 클라이언트 기준

`api:*`는 **클라이언트(admin/app)를 모듈 경계**로 삼는다(팀·도메인이 아니라). 팀 소유권은 모듈을 쪼개지 않고 **모듈 내부를 팀(bounded context) → 도메인 단위 패키지**로 가른다.

- **클라이언트 모듈의 베이스 패키지는 `{basePackage}.api.{client}`** 다(`{client}` = `app`/`admin`). `common-api`만 `{basePackage}` 루트에 공통 인프라를 둔다.
- 그 아래를 **`{팀}.{도메인}`** 으로 나눈다. `{팀}`은 bounded context(예: `welfare`), `{도메인}`은 그 팀이 소유한 개별 도메인(예: `notice`)이다 — `core:domain:{팀}` 모듈의 `domain/{도메인}` 구조(4-3절)를 프레젠테이션에서도 그대로 미러링한다.
- **DTO는 `{도메인}` 아래 `request`/`response` 하위 패키지로 분리**한다. 컨트롤러는 `{도메인}` 패키지 바로 아래 둔다.
- **클라이언트 전역 공통물**(예: `AppApiUser`)은 클라이언트 베이스 패키지에, 그 부속 인프라(ArgumentResolver 등)는 `resolver` 같은 용도별 하위 패키지에 둔다.
- 한 팀이 여러 도메인을 묶을 수 있고(예: core = auth·member), admin·app 양쪽에 컨트롤러를 둘 수 있다. 팀별 파일이 서로 겹치지 않게 한다.
- 여러 팀이 같은 파일을 편집하는 지점은 **라우팅·공통 응답/예외**뿐이며 `common-api`로 한정한다. 보안 설정(`SecurityConfig`의 role→URL 인가)은 `gateway:auth`가 소유한다.
- admin 별도 배포가 필요해지면 `admin-api` + 필요한 도메인을 조립하는 bootstrap을 추가한다(현재는 단일 bootstrap).

```
api/
├── common-api              # 여러 팀이 공유하는 유일한 지점 (라우팅·응답/예외)
│   └── {basePackage}                   # WebMvcConfig, ApiResponse, GlobalExceptionHandler, ApiUser
├── admin-api               # ADMIN /v1/admin/**
│   └── {basePackage}.api.admin
│       ├── (클라이언트 공통물)              # AdminApiUser 등
│       ├── resolver/                   # ArgumentResolver 등 부속 인프라
│       └── {팀}/{도메인}                 # 팀(bounded context) → 도메인 = 소유 단위
└── app-api                 # STUDENT /v1/app/**
    └── {basePackage}.api.app
        ├── (클라이언트 공통물)              # AppApiUser 등
        ├── resolver/
        └── {팀}/{도메인}
```

팀은 `core`(auth·member)·`event`·`internal`·`welfare` 넷이다. 구체 예시 — `core` 팀이 auth·member 두 도메인을, `welfare` 팀이 대여·회비·공지를 소유하며, admin·app 양쪽에 각자의 컨트롤러를 두는 모습:

```
api/
├── common-api
│   └── {basePackage}                          # WebMvcConfig, ApiResponse, GlobalExceptionHandler
├── admin-api
│   └── {basePackage}.api.admin
│       ├── AdminApiUser                       # 클라이언트 공통물
│       ├── resolver
│       │   └── AdminApiUserArgumentResolver
│       ├── core                               # core 팀 (auth·member)
│       │   └── member
│       │       ├── AdminMemberController       #   운영진 회원 관리
│       │       ├── request
│       │       │   └── MemberRegisterRequest
│       │       └── response
│       │           └── MemberResponse
│       └── welfare                            # welfare 팀 (대여·회비·공지)
│           └── rental
│               ├── AdminRentalController       #   대여 승인·반납 처리
│               └── request
│                   └── RentalApproveRequest
└── app-api
    └── {basePackage}.api.app
        ├── AppApiUser
        ├── resolver
        │   └── AppApiUserArgumentResolver
        ├── core                               # core 팀 (auth·member)
        │   ├── auth
        │   │   └── AppAuthController           #   로그인/토큰
        │   └── member
        │       └── AppMemberController         #   내 정보
        └── welfare                            # welfare 팀 (대여·회비·공지)
            └── notice
                ├── AppNoticeController         #   공지 조회
                └── response
                    ├── NoticeDetailResponse
                    └── NoticeListItemResponse
```

- 한 팀 패키지(`core`, `welfare`)의 파일은 그 팀만 건드린다 — admin·app에 흩어져 있어도 소유는 팀 단위다.
- `core` 팀처럼 **여러 도메인(auth·member)을 한 팀이 묶을 수 있다.** 팀 패키지명은 도메인명과 1:1일 필요가 없다.
- 컨트롤러는 접두사(`Admin`/`App`)로 클라이언트를 구분하고, 각 도메인의 공개 `{Domain}Service`(또는 교차 도메인 시 `UseCase`)만 호출한다(5절·6-1절).
- 팀이 겹쳐 충돌하는 지점은 `common-api`의 라우팅·공통 응답과 `gateway:auth`의 `SecurityConfig`뿐이다 — 이 파일들만 변경 시 팀 간 조율이 필요하다.

---

## 3. 모듈 간 의존 방향

**원칙: 모든 화살표는 안쪽(`core`)을 향한다. `core`는 바깥을 모른다.**

```
bootstrap → api:common-api, api:admin-api, api:app-api
bootstrap → gateway:auth, gateway:logging
bootstrap → infrastructure:db, infrastructure:client, infrastructure:outbox
bootstrap → core:common

api:{client}-api → api:common-api
api:*            → core:domain:{도메인}           (공개 인터페이스)
api:*            → core:common
api:*            → gateway:auth                   (DepartmentAccessChecker; PrincipalProvider 구현은 runtimeOnly)
api:*            → gateway:logging                (MDC 키 상수)

gateway:auth     → core:common                    (PrincipalProvider, Role, Department)
gateway:logging  → core:common                    (PrincipalProvider로 userId MDC 주입)

infrastructure:db     → core:domain:{도메인}, core:common   (공개 {Domain}Repository 구현)
infrastructure:client → core:domain:{도메인}, core:common   (공개 {Domain}Client 구현)
infrastructure:outbox → core:common                         (OutboxWriter 구현 + 이벤트 타입, 도메인 모름)

core:domain:{도메인} → core:common                 (+ common.event, OutboxWriter 포트)
core:domain:{도메인} ↛ core:domain:{다른도메인}    (금지)
core:common → (프로젝트 내 의존 없음)
```

- `core:domain:{도메인}`은 `core:common`에만 의존한다. 다른 도메인·web·security·JPA·Modulith에는 의존하지 않는다. Spring은 DI용 `spring-context`, 트랜잭션·이벤트 리스너용 `spring-tx`만 쓴다.
- `infrastructure:*`는 각 도메인의 공개 인터페이스를 구현하므로 해당 `core:domain:{도메인}`에 의존하되(단 `outbox` 제외), 도메인의 `service.impl`에는 접근하지 않는다.
- `core:common`은 어떤 도메인 개념도 담지 않는다(`{Domain}Id` 같은 값 타입 금지). 순수 기술 어휘 + 인증 추상(PrincipalProvider·Role·Department) + 크로스 도메인 이벤트 타입 + 아웃박스 포트만 둔다.
- 모듈 간 의존은 원칙적으로 `implementation`을 쓴다. `api`로 노출하면 전이 의존으로 레이어가 오염된다.

---

## 4. 도메인 경계 규칙

Gradle 모듈 분리가 도메인 간 경계를 컴파일 타임에 막고, Spring Modulith `verify()`가 도메인 내부 공개/비공개 경계를 CI 타임에 막는다.

### 4-1. 두 경계의 강제 성격

- **도메인 간 경계(`{도메인A}` ↛ `{도메인B}`)**: Gradle이 컴파일 타임에 하드 강제한다. 의존을 안 걸면 상대 도메인 클래스는 컴파일 자체가 안 된다.
- **도메인 내부 경계(공개 계층 / `service.impl`)**: 도메인 모듈은 OPEN이라 Modulith가 가리지 않는다. `bootstrap`의 **ArchUnit 테스트(`DomainImplAccessTests`)** 가 CI 타임에 강제한다. 같은 모듈 안이라 Gradle이 못 막고, `impl`에 public 타입이 생기면 다른 모듈에서 import해도 컴파일은 된다 — ArchUnit이 빌드를 깨는 것이 방어선이다.
- 따라서 **`verify()`와 `DomainImplAccessTests`를 `gradle check`와 PR 머지 게이트에 필수로 건다.** 실행하지 않으면 내부 경계는 강제되지 않는다.

### 4-2. 패키지 컨벤션 (탐지 규칙)

- `@Modulithic` 메인 클래스는 `bootstrap`의 `{basePackage}`에 둔다.
- **Gradle 경로와 Java 패키지는 별개다.** 도메인은 Gradle 상 `:core:domain:{도메인}`이지만 패키지는 루트 직속 형제로 둔다.

```
{basePackage}.{도메인}             // ✅ 예: {basePackage}.member  (:core:domain:member)
{basePackage}.core.domain.member  // ❌ Gradle 경로를 반영하면 domain이 한 모듈로 뭉쳐 경계가 안 걸림
```

- `core:common`은 verify 설정에서 shared module로 선언해, 어디서든 의존해도 위반이 나지 않게 한다(어노테이션 없이 순수 Java 유지).
- 도메인이 하나일 때부터 **일부러 위반을 만들어 `verify()`가 실패하는지** 확인한다.

### 4-3. 도메인 모듈의 패키지 구조 & 공개 경계

Gradle 모듈 `core:domain:{모듈}` 하나가 Modulith 애플리케이션 모듈 하나(`{basePackage}.{모듈}`)다. 모듈 안은 **`domain/{도메인}`** 으로 도메인(aggregate) 단위를 나누고, 각 도메인 아래를 **계층 패키지**(`domain`/`repository`/`service`/`service.impl`)로 나눈다. 도메인 객체만 있는 도메인도 계층 패키지를 생략하지 않는다.

Modulith는 모듈 기본 패키지만 공개로, 모든 하위 패키지를 비공개로 보는데 이 구조와 맞지 않는다(공개 계층 패키지마다 `@NamedInterface`를 달아야 함). 그래서 **모듈 기본 패키지의 `package-info.java`에 `@ApplicationModule(type = OPEN)`을 선언해 하위 패키지를 모두 열고**, `service.impl` 차단은 `bootstrap`의 **ArchUnit 테스트(`DomainImplAccessTests`)** 가 담당한다(4-4절).

member 모듈 예시 (도메인 `member`·`notification`):

```
{basePackage}.member                      // Modulith 애플리케이션 모듈 (= core:domain:member)
├── package-info.java                     // @ApplicationModule(type = OPEN)
└── domain/
    ├── member/                           // 도메인(aggregate) 단위
    │   ├── domain/
    │   │   ├── Member, Department, TermType, MemberTermAgreement   // 도메인 객체·enum
    │   │   └── MemberErrorCode                                     // 도메인 에러 코드 (api Swagger 문서화가 참조)
    │   ├── repository/
    │   │   └── MemberRepository          // 아웃바운드 포트 (infrastructure:db가 구현)
    │   └── service/
    │       ├── MemberService             // 공개 진입점
    │       └── impl/                     // 비공개 — 모든 클래스 package-private
    │           └── MemberServiceImpl     // 구현체, 그 외 내부 협력 객체
    └── notification/
        └── domain/
            └── Notification, MemberNotificationSetting
```

```java
// {basePackage}/member/package-info.java
@ApplicationModule(type = ApplicationModule.Type.OPEN)
package kr.ac.kookmin.stream.member;

import org.springframework.modulith.ApplicationModule;
```

- `service.impl` 밖의 클래스는 `service.impl` 안의 타입을 참조하지 않는다. 같은 도메인의 `service` 패키지도, 다른 도메인의 `impl`도 예외가 아니다 — 허용되는 참조는 **같은 `impl` 패키지 안**뿐이다(ArchUnit 규칙 1).
- `service.impl` 안의 모든 클래스는 **package-private**으로 선언한다(ArchUnit 규칙 2). 그래서 다른 모듈에서는 컴파일 자체가 안 되고, 협력 객체도 같은 패키지 안에서만 쓰인다.
- **Repository/Client 인터페이스는 `infrastructure:*`가 구현해야 하므로 `repository` 패키지(공개)에 둔다.** 진입점(`{Domain}Service`)과 아웃바운드 포트(`{Domain}Repository`/`{Domain}Client`)는 공개, 그 구현·로직은 `service.impl`에 비공개.
- 소비 측은 공개 인터페이스를 주입받고, Spring이 런타임에 `{Domain}ServiceImpl`를 연결한다.
- `service.impl` 내부 구조는 각 도메인이 자유롭게 설계한다.
- 새 모듈을 만들면 기본 패키지에 위 `package-info.java`를 함께 만든다. 도메인·계층 패키지를 추가할 때는 아무 선언도 필요 없다.

### 4-4. 검증 테스트

두 테스트가 한 쌍이다. `ModularityTests.verify()`는 모듈 간 순환·의존 규칙을, `DomainImplAccessTests`는 도메인 모듈 안의 `service.impl` 경계를 검사한다.

```java
// bootstrap/src/test/.../ModularityTests.java
class ModularityTests {

    private final ApplicationModules modules = ApplicationModules.of(Application.class);

    @Test
    void verify() {
        modules.verify();
    }

    @Test
    void writeDocs() {
        new Documenter(modules).writeDocumentation();   // spring-modulith-docs: C4/PlantUML 자동 생성
    }
}
```

```java
// bootstrap/src/test/.../DomainImplAccessTests.java (ArchUnit)
@AnalyzeClasses(packages = "{basePackage}", importOptions = ImportOption.DoNotIncludeTests.class)
class DomainImplAccessTests {

    // 규칙 1 — service.impl은 같은 패키지 안에서만 참조한다
    @ArchTest
    static final ArchRule implIsOnlyReferencedFromItsOwnPackage = classes()
        .that().resideInAPackage("..service.impl..")
        .should(ArchConditions.onlyHaveDependentsWhere(originInSamePackageAsTarget()));

    // 규칙 2 — service.impl의 클래스는 package-private으로 둔다
    @ArchTest
    static final ArchRule implHasNoPublicClasses = noClasses()
        .that().resideInAPackage("..service.impl..")
        .should().bePublic();
}
```

---

## 5. 레이어 구조 & 규칙

이 프로젝트는 3개 레이어로 구성한다.

```
Presentation   Controller, Request/Response, UseCase         → api:* 모듈
      ↓
Business       {Domain}Service (공개 인터페이스)               → core:domain:{모듈} domain/{도메인}/service
               {Domain}ServiceImpl (구현체)                   → core:domain:{모듈} domain/{도메인}/service/impl (비공개)
      ↓
Data Access    {Domain}Repository / {Domain}Client 인터페이스  → core:domain:{모듈} domain/{도메인}/repository
                 └ 구현: {Domain}RepositoryImpl / {Domain}JpaRepository → infrastructure:db
                 └ 구현: {Domain}ClientImpl                             → infrastructure:client
```

**레이어 규칙**

1. 레이어는 위에서 아래 방향으로만 참조한다.
2. 역방향 참조를 금지한다.
3. 레이어를 건너뛰는 참조를 금지한다 — Presentation은 Data Access를 직접 참조하지 않고 Business를 거친다.
4. 동일 레이어 간 참조를 금지한다.

- **Business가 Data Access를 직접 참조한다** — `{Domain}ServiceImpl`가 `{Domain}Repository`를 직접 사용한다.
- 비즈니스 규칙 검증 등은 `{Domain}ServiceImpl` 또는 `service.impl` 패키지의 별도 협력 객체에 둔다.
- 도메인 객체는 `core:domain`에 `record`(불변)로 두되 JPA 어노테이션을 갖지 않는다. JPA Entity는 `infrastructure:db`에 둔다.

---

## 6. 도메인 간 통신

도메인끼리 직접 의존이 금지되므로, 교차 도메인 흐름은 두 경로만 사용한다.

### 6-1. 동기 조합 — `api`의 UseCase

교차 도메인 조회·쓰기 흐름은 presentation의 `{Feature}UseCase`가 여러 도메인의 공개 `{Domain}Service`를 조합해 처리한다.

- **`api:*` 모듈에 둔다.** `core`에는 두지 않는다.
- 공개 `{Domain}Service`만 조합한다. `{Domain}Repository`·`service.impl`에는 접근하지 않는다.
- Controller는 UseCase 하나만 참조한다: `Controller → UseCase → 각 도메인 Service`.
- 서로 다른 도메인의 Service 2개 이상을 조합할 때만 UseCase를 만든다. **단일 도메인 흐름은 Controller가 그 `{Domain}Service`를 직접 참조**한다.
- **트랜잭션은 원자성이 필요한 흐름에만 건다.** 유료 행사 신청(행사 정원 차감 + 회비/결제 반영)처럼 전부 성공/전부 실패해야 하는 경우에만 UseCase 메서드에 `@Transactional`을 선언한다(동일 DataSource 기준 한 트랜잭션). 운영진 대시보드 같은 조회 조합에는 트랜잭션을 걸지 않는다.
- UseCase 트랜잭션이 도메인 Service를 감싸려면 `{Domain}Service`는 기본 전파(REQUIRED)를 쓴다. REQUIRES_NEW는 원자성을 깨므로 쓰지 않는다.
- 롤백 불가한 외부 부수효과(외부 결제 PG 호출 등)가 끼면 트랜잭션으로 원자성을 보장할 수 없다. 그런 UseCase에 한해 보상 로직을 명시한다.

### 6-2. 비동기 반응 — 이벤트 + 직접 구현 아웃박스

한 도메인의 사실(fact)에 다른 도메인이 느슨하게 반응해야 할 때 사용한다(예: 행사 신청 확정 → 공지/알림 반영). Spring Modulith 이벤트 레지스트리를 쓰지 않고, 순수 Spring 이벤트 + 직접 구현한 트랜잭셔널 아웃박스로 at-least-once를 보장한다.

```
발행 (예: event 도메인, 자기 트랜잭션 내)
  EventServiceImpl → outboxWriter.record(event)              // core:common 포트
      ↓ 같은 트랜잭션에서 outbox 테이블에 저장 (원 비즈니스 쓰기와 원자적)
릴레이 (infrastructure:outbox)
  OutboxRelay(@Scheduled 폴러) → 미발행 레코드 조회
      → applicationEventPublisher.publishEvent(event)        // 인프로세스 재발행
      → 전달 성공 시 발행 완료 마킹, 실패 시 다음 폴에서 재시도
구독 (예: welfare 도메인 — 공지/알림 반영)
  @TransactionalEventListener void on(EventApplicationConfirmed e)  // spring-tx, Modulith 의존 없음
```

- **이벤트 타입은 `core:common.event`에 둔다.** 발행/구독 도메인이 서로를 의존하지 않고 이벤트 타입만 공유한다.
- 이벤트는 **사실**이다. ID·원시값만 담고 애그리거트/값 타입을 싣지 않는다. 커맨드(요청)를 이벤트로 쓰지 않는다.
- **발행은 `OutboxWriter` 포트로 기록**한다(원 비즈니스 트랜잭션과 원자적). 인프로세스 재발행은 릴레이만 수행한다.
- **구독은 `@TransactionalEventListener`**(spring-tx)로 한다. `core:domain`에 Modulith 이벤트 의존을 넣지 않는다.
- 소비 리스너는 **멱등**하게 작성한다(at-least-once).
- 아웃박스 테이블 스키마는 `flyway-migration.md`로 관리한다.
- 최종적 일관성 경로다. "한 요청 안에서 쓰기 직후 결과를 즉시 조회해 응답에 실어야" 하면 이벤트가 아니라 6-1의 동기 UseCase로 처리한다.

---

## 7. 모듈별 허용 의존성

| 모듈 | 허용 의존성 |
| --- | --- |
| `bootstrap` | 모든 `api:*` + `gateway:*` + `infrastructure:*` + `core:common` + Spring Boot 실행(Actuator, 스케줄링) + `spring-modulith-core`·ArchUnit(**test scope**) |
| `api:common-api` | `core:domain:{도메인}` + `core:common` + `gateway:auth`(DepartmentAccessChecker) + `gateway:logging` + Spring MVC + validation |
| `api:{client}-api` | `api:common-api` + `core:domain:{도메인}` + `core:common` + `gateway:auth` + `gateway:logging` + Spring MVC + validation |
| `core:domain:{도메인}` | `core:common` + `spring-context`(DI) + `spring-tx` + `spring-modulith-api`(**compileOnly**, `@ApplicationModule` 선언용) + 순수 Java. **Modulith core·web·security·JPA 없음** |
| `core:common` | 순수 Java / 유틸리티만. Spring·Modulith·web·security·JPA 없음 |
| `gateway:auth` | `core:common` + Spring Security + `jjwt` + `spring-webmvc`(예외 위임) |
| `gateway:logging` | `core:common` + `spring-web` + `spring-context` + slf4j 등 + Servlet API |
| `infrastructure:db` | `core:domain:{도메인}` + `core:common` + JPA / Flyway / MySQL 드라이버 |
| `infrastructure:client` | `core:domain:{도메인}` + `core:common` + `spring-web`/RestClient, Jackson 등 |
| `infrastructure:outbox` | `core:common` + `spring-context`(스케줄링·`ApplicationEventPublisher`) + JPA. **도메인 모듈 의존 없음** |

---

## 8. 확장 · 미결

- **도메인 `service.impl` 패키지 구조**: 각 도메인이 자유롭게 설계한다(4-3절 제약만 만족).
- **`common.event` 분가**: 크로스 도메인 이벤트가 늘면 `core:event-contract`로 분리.
- **아웃박스 외부화**: 브로커(Kafka 등) 필요 시 릴레이 재발행 대상을 확장. 현재는 인프로세스 재발행만.
- **레거시 role 흡수**: 기존 `USER/ADMIN/WORKER/GA` 시스템의 데이터 흡수·마이그레이션은 `role-migration-plan.md`에 계획으로만 정리(미구현).
- **UseCase 재사용 한계**: UseCase는 api(HTTP)에 묶인다. 비-HTTP 트리거 요구가 생기면 배치 재검토.
