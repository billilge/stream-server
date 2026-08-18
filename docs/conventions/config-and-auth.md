# Config & Auth — 설정 바인딩 & 인증/권한 컨벤션 (Java 21)

> 이 문서는 모듈별 설정 관리, `@ConfigurationProperties` 바인딩, `PrincipalProvider` 인증 흐름, role/부서(Department) 권한 모델을 다룬다.
> 설정값 추가, 인증·인가가 필요한 API 작업 시 참조한다.
> 레거시 시스템(`USER/ADMIN/WORKER/GA`) 흡수 마이그레이션은 `role-migration-plan.md`(계획, 미구현) 참조.

---

## 1. 모듈별 yml 관리

각 모듈은 자신의 설정값을 `src/main/resources/application-{모듈명}.yml`에 정의하고, `bootstrap`의 `application.yml`에서 `spring.config.import`로 가져온다.

```yaml
# bootstrap/application.yml
spring:
  config:
    import:
      - classpath:application-gateway-auth.yml
      - classpath:application-common-api.yml
```

> `bootstrap`에 설정을 직접 작성하지 않는다. 설정 소유권은 해당 모듈에 있다.
> 각 모듈 yml이 classpath에 올라오려면 `bootstrap`이 그 모듈을 `implementation`으로 **직접** 의존해야 한다.

---

## 2. 환경변수 관리

yml에서 `${ENV_VAR}`로 참조할 때는 반드시 루트 `.env.example`에도 항목을 추가한다.

```yaml
# application-gateway-auth.yml
jwt:
  secret-key: ${JWT_SECRET_KEY}
  issuer: ${JWT_ISSUER}
  access-token-expiry: ${JWT_ACCESS_TOKEN_EXPIRY:3600000}
```

- 기본값(`${VAR:default}`)이 있으면 `.env.example`에도 동일 값을 예시로 기재한다.
- 필수값(`${VAR}`)은 의도를 나타내는 플레이스홀더를 쓴다.
- `.env.example`은 커밋하고, 실제 값이 담긴 `.env`는 커밋하지 않는다.

---

## 3. 설정 바인딩

- `@ConfigurationProperties`를 사용한다(`@Value` 금지). `record`로 선언하고 `bootstrap`에서 `@ConfigurationPropertiesScan`으로 활성화한다.

```java
// gateway:auth — JwtProperties.java
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
    String secretKey,
    String issuer,
    long accessTokenExpiry
) {}
```

---

## 4. 권한 모델 (2계층)

이 플랫폼은 단일 학생회이므로 테넌트 식별자(매장/학생회 ID 등)가 없다. 권한은 두 계층으로 나눈다.

1. **기본 role — `STUDENT` / `ADMIN`**: URL 패턴 인가의 축. 학생 앱(`/v1/app/**`)은 `STUDENT`, 운영진 콘솔(`/v1/admin/**`)은 `ADMIN`으로 1차 인가한다.
2. **부서(Department)**: `ADMIN`에게만 추가로 부여되는 세밀 권한. 학생회 부서 단위 authority로, `DEPT_` 접두사를 붙여 role과 구분한다(예: `DEPT_GENERAL_AFFAIRS`). 사물함 승인은 복지부만, 회비는 총무부만 같은 부서 단위 인가에 쓴다.

```java
// core:common — Role.java
public enum Role { STUDENT, ADMIN }

// core:common — Department.java (예시 — 실제 부서 목록 확정 후 반영)
public enum Department {
    GENERAL_AFFAIRS,   // 총무부
    PLANNING,          // 기획부
    PR,                // 홍보부
    WELFARE            // 복지부
    // ...
}
```

> 한 관리자의 Spring Security 권한은 `ADMIN` + `DEPT_*`(소속 부서만큼) 형태로 조립된다. `STUDENT`에는 부서가 없다.

### 4-1. 토큰 claim

Access Token(HS256) 단일 발급. 테넌트 claim은 없다.

| Claim | 타입 | 설명 |
| --- | --- | --- |
| `iss` | String | `jwt.issuer` |
| `sub` | String | userId (숫자 문자열) |
| `iat` / `exp` | Instant | 발급·만료 |
| `roles` | List\<String\> | `Role.name()` 목록 (`STUDENT` 또는 `ADMIN`) |
| `departments` | List\<String\> | `Department.name()` 목록. **`ADMIN`만 채워지며, `STUDENT`는 빈 배열** |

### 4-2. PrincipalProvider

`core:common`에 인증 추상을 두고, `gateway:auth`가 `SecurityContextHolder` 기반으로 구현한다.

```java
// core:common — PrincipalProvider.java
public interface PrincipalProvider {
    Long userId();
    Set<Role> roles();
    Set<Department> departments();   // STUDENT면 빈 Set
}
```

### 4-3. 인증/인가 흐름

```
HTTP Request (Authorization: Bearer <token>)
    ↓
JwtAuthFilter                   — 토큰 검증 → SecurityContextHolder에 UserAuthentication 저장
                                  (권한: ADMIN/STUDENT + DEPT_*)
    ↓
SecurityConfig                  — URL 패턴별 hasAuthority(Role)로 1차 인가
                                  (/v1/admin/** → ADMIN, /v1/app/** → STUDENT)
    ↓
{Role}ApiUserArgumentResolver   — 해당 role 없으면 BusinessException(FORBIDDEN),
                                  있으면 {Role}ApiUser 스냅샷 생성 후 주입
    ↓
Controller / UseCase            — 부서 단위 인가는 DepartmentAccessChecker로 별도 검증 (4-4절)
```

> `authorizeHttpRequests` 규칙은 선언 순서대로 매칭되므로 role별 패턴을 `anyRequest`보다 먼저 선언한다.
> 권한 문자열은 `Role.X.name()`(예: `"ADMIN"`)을 그대로 쓴다. `"ROLE_"` 접두사가 없으므로 `hasRole()`이 아니라 `hasAuthority()`를 쓴다.

### 4-4. 부서(Department) 인가 — 별도 검증 클래스

부서 단위 인가는 인증 컨텍스트(누가 어떤 부서 권한을 갖는가)에 의존하므로 **`gateway:auth`의 별도 클래스 `DepartmentAccessChecker`**로 검증한다. `core:domain`에 두지 않는다(도메인이 인증 개념을 알면 안 됨). api의 UseCase/Controller가 주입받아 호출한다.

```java
// gateway:auth — DepartmentAccessChecker.java
@Component
@RequiredArgsConstructor
public class DepartmentAccessChecker {

    private final PrincipalProvider principalProvider;

    // 요구 부서 권한이 없으면 403
    public void requireDepartment(Department required) {
        if (!principalProvider.departments().contains(required)) {
            throw new BusinessException(CommonErrorCode.FORBIDDEN);
        }
    }
}
```

```java
// api:admin-api — 사물함 승인은 복지부만
@Component
@RequiredArgsConstructor
public class LockerApprovalUseCase {

    private final DepartmentAccessChecker departmentAccessChecker;
    private final LockerService lockerService;

    public void approve(Long userId, Long applicationId) {
        departmentAccessChecker.requireDepartment(Department.WELFARE);
        lockerService.approve(applicationId);
    }
}
```

> 정적 부서 권한만 필요한 단순 엔드포인트는 `@PreAuthorize("hasAuthority('DEPT_WELFARE')")`로 선언적으로 걸어도 된다. 그러나 데이터에 따라 달라지는 인가나 여러 부서 조합 검증은 `DepartmentAccessChecker`를 사용한다 — 이 프로젝트의 기본은 `DepartmentAccessChecker`다.

### 4-5. 모듈별 역할

| 모듈 | 파일 | 역할 |
| --- | --- | --- |
| `core:common` | `auth/PrincipalProvider.java` | 인터페이스 (`userId()`, `roles()`, `departments()`) |
| `core:common` | `auth/Role.java` | `STUDENT`, `ADMIN` |
| `core:common` | `auth/Department.java` | 부서 enum (`DEPT_*` authority의 원천) |
| `gateway:auth` | `security/SecurityPrincipalProvider.java` | `SecurityContextHolder` 기반 구현 |
| `gateway:auth` | `security/DepartmentAccessChecker.java` | 부서 인가 검증 (별도 클래스) |
| `gateway:auth` | `jwt/JwtProvider.java` / `jwt/JwtAuthFilter.java` | JWT 생성·파싱 / 토큰 검증 |
| `gateway:auth` | `config/SecurityConfig.java` | FilterChain, URL 패턴별 role 인가 |
| `api:common-api` | `common/ApiUser.java` | 인증 사용자 스냅샷 인터페이스 |
| `api:{client}-api` | `{role}/{Role}ApiUser.java`, `.../resolver/{Role}ApiUserArgumentResolver.java` | role 전용 `ApiUser` + 주입 |

---

## 5. 사용 규칙

### Controller

어노테이션 없이 자신이 속한 role 모듈의 `ApiUser` 구현체로 선언하면 자동 주입된다.

```java
// api:app-api
@PostMapping
public ApiResponse<Void> apply(
    StudentApiUser apiUser,
    @Valid @RequestBody RentalApplyRequest request
) {
    rentalService.apply(apiUser.userId(), request.toCommand());
    return ApiResponse.success();
}
```

### Swagger 인터페이스(`*Api`)

`@Parameter(hidden = true)`를 붙인다.

```java
ApiResponse<Void> apply(
    @Parameter(hidden = true) StudentApiUser apiUser,
    @Valid @RequestBody RentalApplyRequest request
);
```

### Service

`apiUser.userId()`를 꺼내 `Long`으로 전달한다. Service는 `ApiUser`에 의존하지 않는다.

### 인증 불필요 API

`SecurityConfig`에 `permitAll`을 추가하고, Controller에서 `ApiUser` 파라미터를 선언하지 않는다.

```java
http.authorizeHttpRequests(auth -> auth
    .requestMatchers("/auth/login").permitAll()
    .requestMatchers("/v1/admin/**").hasAuthority(Role.ADMIN.name())
    .requestMatchers("/v1/app/**").hasAuthority(Role.STUDENT.name())
    .anyRequest().authenticated()
);
```

### 테스트 환경

`test` 프로파일에서 `PrincipalProvider` 스텁을 `@Primary`로 등록한다.

```java
@Profile("test")
@Primary
@Component
public class StubPrincipalProvider implements PrincipalProvider {
    @Override public Long userId() { return 1L; }
    @Override public Set<Role> roles() { return Set.of(Role.ADMIN); }
    @Override public Set<Department> departments() { return Set.of(Department.WELFARE); }
}
```
