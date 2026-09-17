# Coding Style — 네이밍 & 클래스 작성 규칙 (Java 21)

> 이 문서는 클래스 네이밍, 도메인/DTO/Command/Entity/Repository/Service/UseCase 작성 패턴, Validation 규칙을 다룬다.
> 실제 코드 생성/리뷰 시 참조한다. 모듈 배치·레이어 규칙은 `architecture.md`, 예외는 `error-handling.md`를 함께 본다.
> 예시 도메인은 `member`(학생 회원, `core:domain:member`)를 사용한다.

---

## 1. 네이밍 컨벤션

Business는 공개 인터페이스 + `service.impl` 구현체로 구성한다. 도메인 모듈 안의 패키지는 `domain/{도메인}/{domain|repository|service|service.impl}`로 나누며, `service.impl` 안의 클래스는 모두 package-private으로 둔다(`architecture.md` 4-3절).

| 레이어 | 역할 | 네이밍 | 위치 |
| --- | --- | --- | --- |
| Presentation | HTTP 요청 처리 | `{Client}{Domain}Controller` (예: `AppMemberController`, `AdminMemberController`) | `api:{client}-api` |
| Presentation | 요청 객체 | `{Domain}{Action}Request` | `api:{client}-api` |
| Presentation | 응답 객체 | `{Domain}{Action}Response` | `api:{client}-api` |
| Presentation | 교차 도메인 조합 | `{Feature}UseCase` | `api:{client}-api` |
| Business | 공개 진입점 인터페이스 | `{Domain}Service` | `core:domain:{모듈}` `domain/{도메인}/service` (공개) |
| Business | 구현체 | `{Domain}ServiceImpl` | `core:domain:{모듈}` `domain/{도메인}/service/impl` (비공개) |
| Data Access | Repository 인터페이스 | `{Domain}Repository` | `core:domain:{모듈}` `domain/{도메인}/repository` (공개) |
| Data Access | Repository 구현체 | `{Domain}RepositoryImpl` | `infrastructure:db` |
| Data Access | JPA Repository | `{Domain}JpaRepository` | `infrastructure:db` |
| Data Access | 조회 전용 프로젝션 | `{조회내용}Projection` (예: `EventApplicantCountProjection`) | `infrastructure:db` |
| Data Access | 외부 API 클라이언트 인터페이스 | `{Domain}Client` | `core:domain:{모듈}` `domain/{도메인}/repository` (공개) |
| Data Access | Client 구현체 | `{Domain}ClientImpl` | `infrastructure:client` |

---

## 2. 클래스 작성 규칙

### 2-1. 도메인 객체

- `record`로 선언한다(불변). JPA 어노테이션을 포함하지 않는다.
- 의미적으로 묶이는 필드가 여럿이면 VO로 그룹화한다. VO도 `record`로 선언하고 같은 도메인 패키지에 둔다.

```java
// core:domain:member
public record Member(
    Long id,
    String studentNo,   // 학번
    String name
) {}
```

### 2-2. DTO / Command

- Request/Response DTO는 `record`(`api:{client}-api`). 각 도메인 패키지 아래 `request`/`response` 하위 패키지로 나눠 둔다(`{basePackage}.api.{client}.{팀}.{도메인}.{request|response}`, `architecture.md` 2-2절).
- Request DTO를 Service로 그대로 넘기지 않는다. `toCommand()`로 Command(`core:domain`)로 변환한다.

```java
// api:admin-api
public record MemberRegisterRequest(
    String studentNo,
    String name
) {
    public MemberRegisterCommand toCommand() {
        return new MemberRegisterCommand(studentNo, name);
    }
}

public record MemberResponse(
    Long memberId,
    String studentNo,
    String name
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(member.id(), member.studentNo(), member.name());
    }
}

// core:domain:member
public record MemberRegisterCommand(String studentNo, String name) {}
```

**오직 하나의 Response에서만 쓰이는 하위 DTO**는 별도 파일로 빼지 않고 그 Response 안에 중첩 `record`로 선언한다. 다른 곳에서도 쓰이게 되면 그 시점에 최상위 파일로 승격한다.

```java
// api:app-api
public record NoticeDetailResponse(
    Long noticeId,
    List<Image> images
) {
    public record Image(Long fileId, String fileUrl) {
        public static Image from(Long fileId) {
            return new Image(fileId, null);
        }
    }
}
```

**공통 응답 래퍼 (`ApiResponse`, `api:common-api`)** — `private` 생성자 + 정적 팩토리.

```java
// api:common-api
public final class ApiResponse<T> {

    private final boolean success;
    private final String code;
    private final String message;
    private final T data;

    private ApiResponse(boolean success, String code, String message, T data) {
        this.success = success; this.code = code; this.message = message; this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "SUCCESS", "요청에 성공했습니다.", data);
    }
    public static <T> ApiResponse<T> success() { return success(null); }
    // 에러 팩토리는 error-handling.md 5절 참조

    public boolean isSuccess() { return success; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
```

### 2-3. 레이어 간 변환

- `core → api`(도메인 객체 → Response)는 Response record의 정적 팩토리 `from(...)`으로 정의한다. `core:domain`은 `api` 타입에 의존하지 않는다.
- `api → core`(Request → Command)는 Request record의 `toCommand()`로 정의한다.

### 2-4. 목록 조회 응답 — 커서 기반 vs 오프셋 기반

`core:common`의 `{X}Result`와 `api:common-api`의 `{X}Response` 쌍으로 구성한다.

| 방식 | 사용 시점 | 타입 |
| --- | --- | --- |
| 커서/슬라이스 | 무한 스크롤 등 다음 페이지 여부 + 커서만 필요 | `CursorSliceResult<T>` / `CursorSliceResponse<T>` |
| 오프셋 | 페이지 번호·전체 개수·전체 페이지 필요 | `PageResult<T>` / `PageResponse<T>` |

- 커서 기반 응답 필드는 항상 `content`/`hasNext`/`nextCursor`로 통일한다.
- `nextCursor`는 **클라이언트에게 불투명한 문자열**이다. 정렬 키가 여러 개인 keyset 커서를 담아야 하므로 도메인이 `{Domain}Cursor` record로 정렬 키와 그 문자열 표현(`format()`/`from(String)`)을 소유하고, Base64 URL-safe 인코딩은 웹 계층에서 `CursorCodec`(`api:common-api`)으로 처리한다.

```java
// core:common
public record CursorSliceResult<T>(List<T> content, boolean hasNext, String nextCursor) {}
public record PageResult<T>(List<T> content, int page, int size, long totalCount, int totalPage) {}
```

### 2-5. JPA Entity

- `infrastructure:db`에 선언한다. 도메인 객체를 받는 정적 팩토리 `from(...)`과 `toDomain()`을 제공하고, 용도에 맞는 Base Entity를 상속한다. JPA용 기본 생성자는 `@NoArgsConstructor(access = AccessLevel.PROTECTED)`로 둔다(2-10·2-11절).

**Base Entity 선택 기준** (`infrastructure:db` 공통 패키지)

| 클래스 | 제공 필드 | 사용 시점 |
| --- | --- | --- |
| `BaseCreatedTimeEntity` | `createdAt` | 생성 시각만 필요 |
| `BaseTimeEntity` | `createdAt`, `updatedAt` | 일반 엔티티 (기본값) |
| `BaseSoftDeleteEntity` | `createdAt`, `updatedAt`, `deletedAt` | 소프트 삭제 필요 |

- `BaseSoftDeleteEntity`는 `BaseTimeEntity`를 상속한다.
- 소프트 삭제는 `@Transactional` 범위에서 `entity.delete()` → dirty checking으로 반영한다.
- 삭제 제외 조회(`where deleted_at is null`)는 각 `{Domain}JpaRepository`에서 처리한다.
- **소프트 삭제 컬럼(`deleted_at`)의 인덱스·유니크 처리는 MySQL 규칙을 따른다** — 단독 인덱스 금지, 복합 인덱스의 trailing, 유니크는 generated column. 상세는 `flyway-migration.md` 3-4절.

```java
// infrastructure:db
@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)   // JPA
public class MemberJpaEntity extends BaseSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentNo;
    private String name;

    private MemberJpaEntity(Member member) {
        this.id = member.id();
        this.studentNo = member.studentNo();
        this.name = member.name();
    }

    public static MemberJpaEntity from(Member member) {
        return new MemberJpaEntity(member);
    }

    public Member toDomain() {
        return new Member(id, studentNo, name);
    }
}
```

**다른 aggregate 참조 컬럼**

- 다른 도메인(aggregate) 참조는 JPA 연관관계 대신 단순 `Long` 필드(`memberId`, `itemId` 등)로 선언한다. DB에도 FK를 걸지 않는다(`flyway-migration.md` 3-2절).

**JSON 컬럼 (MySQL)**

```java
@JdbcTypeCode(SqlTypes.JSON)
@Column(columnDefinition = "json")
private MemberProfile profile;   // record VO를 JSON 컬럼으로
```

### 2-6. Repository

- `core:domain`에 인터페이스만 공개로 선언하고, 부재는 `Optional`로 표현한다.

```java
// core:domain:member — domain/member/repository (공개)
public interface MemberRepository {
    Optional<Member> findById(Long id);
    boolean existsByStudentNo(String studentNo);
    Member save(Member member);
}
```

```java
// infrastructure:db
public interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, Long> {
    boolean existsByStudentNo(String studentNo);
}

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Optional<Member> findById(Long id) {
        return memberJpaRepository.findById(id).map(MemberJpaEntity::toDomain);
    }

    @Override
    public boolean existsByStudentNo(String studentNo) {
        return memberJpaRepository.existsByStudentNo(studentNo);
    }

    @Override
    public Member save(Member member) {
        return memberJpaRepository.save(MemberJpaEntity.from(member)).toDomain();
    }
}
```

**조회 전용 프로젝션** — 일부 컬럼이나 집계 결과만 필요하면 `record`로 받고 JPQL 생성자 표현식으로 채운다. `List<Object[]>`는 무엇이 담겼는지 드러나지 않아 쓰지 않는다.

- 이름은 `{조회내용}Projection`으로 끝낸다. `Row`·`Dto`는 쓰지 않는다.
- `infrastructure:db`에 두고 `core:domain` 밖으로 내보내지 않는다. 도메인 변환은 `{Domain}RepositoryImpl`이 한다.

```java
// infrastructure:db
public record EventApplicantCountProjection(Long eventId, Long applicantCount) {}

@Query("""
    SELECT new kr.ac.kookmin.stream.db.event.EventApplicantCountProjection(a.eventId, COUNT(a))
    FROM EventApplicationJpaEntity a
    WHERE a.eventId IN :eventIds
    GROUP BY a.eventId
    """)
List<EventApplicantCountProjection> countApplicantsByEventIds(@Param("eventIds") List<Long> eventIds);
```

**레포지토리는 조합하지 않는다** — 여러 조회 결과를 짝짓는 일은 `{Domain}ServiceImpl`이 한다. 레포지토리는 각각 그대로 돌려준다. 그래야 조합을 DB 없이 단위 테스트할 수 있고, 호출부가 필요한 조회만 고를 수 있다.

```java
// ❌ 레포지토리가 행사와 신청자 수를 짝지어 반환
CursorSliceResult<EventApplicantCount> findPublishedSlice(...);

// ✅ 각각 반환하고 서비스가 조합
CursorSliceResult<Event> findPublishedSlice(...);
Map<Long, Long> countAppliedByEventIds(List<Long> eventIds);
```

---

### 2-7. Business Layer (Service)

- `{Domain}Service`는 공개 인터페이스(`service`), `{Domain}ServiceImpl`는 구현체(`service.impl`). `service.impl` 안의 클래스는 구현체·협력 객체 모두 **package-private**으로 선언한다(package-private `@Service`도 빈 등록됨). ArchUnit(`DomainImplAccessTests`)이 public 클래스와 외부 참조를 잡는다.
- **`{Domain}ServiceImpl`가 `{Domain}Repository`를 직접 참조**한다.
- 비즈니스 규칙 검증은 `{Domain}ServiceImpl`(또는 `service.impl` 협력 객체)에서 하고 `BusinessException`을 던진다.
- 트랜잭션 경계는 Service 메서드에. 조회 전용은 `@Transactional(readOnly = true)`, 교차 도메인 UseCase가 감쌀 수 있게 **기본 전파(REQUIRED)** 를 쓴다(`architecture.md` 6-1절).

```java
// core:domain:member — domain/member/service (공개)
public interface MemberService {
    Member register(MemberRegisterCommand command);
    Member getById(Long id);
}
```

```java
// core:domain:member — domain/member/service/impl (비공개)
@Service
@RequiredArgsConstructor
class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Member register(MemberRegisterCommand command) {
        // 학번 중복 등 비즈니스 규칙 검증 후 BusinessException
        if (memberRepository.existsByStudentNo(command.studentNo())) {
            throw new BusinessException(MemberErrorCode.MEMBER_ALREADY_EXISTS);
        }
        Member member = Member.register(command.studentNo(), command.name());
        return memberRepository.save(member);
    }

    @Override
    @Transactional(readOnly = true)
    public Member getById(Long id) {
        return memberRepository.findById(id)
            .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
```

### 2-8. Controller

- role 전용 `ApiUser`(`config-and-auth.md`)와 `{Domain}Service`를 주입받는다. 단일 도메인 흐름은 Controller가 직접 처리한다.
- 클라이언트 접두사(`Admin`/`App`)로 컨트롤러를 구분하고, 각 클라이언트 모듈의 `{basePackage}.api.{client}.{팀}.{도메인}` 패키지 바로 아래 둔다(DTO는 그 아래 `request`/`response`로 분리, `architecture.md` 2-2절).

```java
// api:admin-api — 운영진 회원 등록
@RestController
@RequestMapping("/v1/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

    private final MemberService memberService;

    @PostMapping
    public ApiResponse<MemberResponse> register(@Valid @RequestBody MemberRegisterRequest request) {
        Member member = memberService.register(request.toCommand());
        return ApiResponse.success(MemberResponse.from(member));
    }
}
```

```java
// api:app-api — 학생 내 정보 조회
@RestController
@RequestMapping("/v1/app/members")
@RequiredArgsConstructor
public class AppMemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ApiResponse<MemberResponse> me(StudentApiUser apiUser) {
        Member member = memberService.getById(apiUser.userId());
        return ApiResponse.success(MemberResponse.from(member));
    }
}
```

### 2-9. UseCase (교차 도메인 조합)

- **서로 다른 도메인의 `{Domain}Service` 2개 이상**을 조합할 때만 만든다. `api:{client}-api`에 두고 공개 `{Domain}Service`만 조합한다.
- **`{도메인}` 아래 `usecase` 하위 패키지에 둔다**(`{basePackage}.api.{client}.{팀}.{도메인}.usecase`, `request`/`response`와 동일 계층, `architecture.md` 2-2절). 컨트롤러는 `{도메인}` 패키지 바로 아래 있으므로 UseCase와 계층이 분리된다.
- 트랜잭션은 원자성이 필요한 흐름에만. 조회 조합은 걸지 않거나 `readOnly`, 원자적 쓰기에만 `@Transactional`(`architecture.md` 6-1절).

```java
// api:admin-api — 조회 조합 (운영진 대시보드)
@Component
@RequiredArgsConstructor
public class AdminDashboardUseCase {

    private final MemberService memberService;
    private final EventService eventService;
    private final NoticeService noticeService;

    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboard() {
        long memberCount = memberService.countActive();
        List<Event> upcoming = eventService.findUpcoming();
        List<Notice> recentNotices = noticeService.findRecent();
        return AdminDashboardResponse.of(memberCount, upcoming, recentNotices);
    }
}
```

```java
// api:app-api — 원자적 쓰기 오케스트레이션 (유료 행사 신청)
@Component
@RequiredArgsConstructor
public class EventApplicationUseCase {

    private final EventService eventService;
    private final FeeService feeService;

    @Transactional  // 정원 차감 + 회비 반영을 원자적으로
    public EventApplicationResponse apply(Long memberId, EventApplyCommand command) {
        Event event = eventService.apply(memberId, command);      // 정원 차감·신청 등록
        feeService.charge(memberId, event.participationFee());     // 참가비 반영
        return EventApplicationResponse.from(event);
    }
}
```

### 2-10. 객체 생성 — 정적 팩토리 메서드

객체는 `new`로 직접 만들지 않고 정적 팩토리 메서드로 생성한다. 생성자는 `private`(JPA처럼 프레임워크가 요구하면 `protected`)으로 감춘다.

| 이름 | 쓰임 | 예 |
| --- | --- | --- |
| `from` | 다른 타입 하나를 받아 변환 | `MemberResponse.from(member)`, `MemberJpaEntity.from(member)` |
| `of` | 값 여러 개를 받아 조합 | `JwtAuthFilter.of(jwtProvider, handlerExceptionResolver)` |
| `create` | 새로 만든다는 의미를 드러낼 때 | `Member.create(studentNo, name)` |

```java
// gateway:auth
@Getter
@Accessors(fluent = true)
public class UserAuthentication extends AbstractAuthenticationToken {

    private final Long userId;

    private UserAuthentication(JwtPayload payload) {
        super(toAuthorities(payload));
        this.userId = payload.userId();
        setAuthenticated(true);
    }

    public static UserAuthentication from(JwtPayload payload) {
        return new UserAuthentication(payload);
    }
}
```

- 이름이 생성 의도를 드러내므로, 인자 목록만으로는 구분되지 않는 여러 생성 경로를 표현할 수 있다.
- 생성자를 감추면 호출부가 `new`로 우회할 수 없어 생성 경로가 하나로 모인다.
- **예외** — 아래는 `new`를 그대로 쓴다.
  - `record`(도메인 객체·Command·Request/Response): 표준 생성자를 쓴다. 단 타입 변환이 끼면 `from(...)`/`toCommand()`를 둔다(2-2·2-3절).
  - 예외 클래스: `throw new BusinessException(...)`.
  - 스프링이 생성·주입하는 빈: 애초에 직접 생성하지 않는다(2-11절).

### 2-11. Lombok

루트 `build.gradle.kts`에서 전 모듈에 적용된다. 모듈별 `build.gradle.kts`에 다시 선언하지 않는다. 동작 설정은 루트 `lombok.config`에 둔다.

| 어노테이션 | 용도 |
| --- | --- |
| `@Getter` | 필드 접근자. 클래스 단위로 붙인다 |
| `@Accessors(fluent = true)` | `getXxx()` 대신 `xxx()` 접근자. `record`와 표기를 맞출 때 (`CommonErrorCode`, `UserAuthentication`) |
| `@RequiredArgsConstructor` | 스프링 빈(`@Service`/`@Repository`/`@Component`/`@RestController`)의 생성자 주입 |
| `@RequiredArgsConstructor(access = AccessLevel.PRIVATE)` | 정적 팩토리(2-10절)와 짝지어 생성자를 감출 때 |
| `@NoArgsConstructor(access = AccessLevel.PROTECTED)` | JPA Entity의 기본 생성자 |
| `@AllArgsConstructor` | 필드를 갖는 enum (`{Domain}ErrorCode`) |

- `@Data`·`@Setter`는 쓰지 않는다. 객체는 불변을 기본으로 하고, 상태 변경은 의도가 드러나는 메서드(`entity.delete()` 등)로 표현한다.
- `record`에는 Lombok을 붙이지 않는다. 접근자·`equals`/`hashCode`가 이미 제공된다.
- 스프링 빈의 생성자 주입은 **항상 `@RequiredArgsConstructor`**로 한다. 생성자를 직접 쓰는 경우는 하나뿐이다 — 주입받은 값으로 다른 필드를 초기화해야 할 때. 예: `JwtProperties`로 `SecretKey`를 만드는 `JwtProvider`.
- 주입할 빈을 지목해야 하면 **필드에** `@Qualifier`를 붙인다. 루트 `lombok.config`의 `lombok.copyableAnnotations`가 이를 생성자 파라미터로 복사한다.

```java
// gateway:auth — HandlerExceptionResolver 빈이 여럿이라 이름으로 지목
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver handlerExceptionResolver;
}
```

```properties
# lombok.config (루트)
config.stopBubbling = true
lombok.copyableAnnotations += org.springframework.beans.factory.annotation.Qualifier
```

### 2-12. Client (외부 스토리지·API 클라이언트)

Repository(2-6절)와 같은 구조다 — `core:domain`에 인터페이스(공개), `infrastructure:client`에 구현체.

```java
// core:domain:internal — domain/file/client (공개)
public interface FileStorageClient {
    UploadUrl issuePresignedUrl(String fileKey, String contentType);
    void write(String fileKey, InputStream content);
    void deleteObject(String fileKey);
}
```

- **구현체가 여러 개이고 그중 일부 메서드가 특정 구현체에서 의미가 없으면, 인터페이스를 쪼개지 않고 그 구현체에서 `UnsupportedOperationException` + 사유 주석으로 막는다.** 인터페이스 분리는 그 구현체가 계속 쓰일 때만 이득이 크다 — 임시 구현체처럼 나중에 통째로 걷어낼 코드라면 지금 쪼개봤자 걷어낼 때 그 분리도 같이 없어진다.

```java
// infrastructure:client — S3FileStorageClient
// S3는 클라이언트가 presigned URL로 직접 업로드하므로 서버가 파일 바이트를 받을 일이 없다
@Override
public void write(String fileKey, InputStream content) {
    throw new UnsupportedOperationException("S3는 클라이언트가 presigned URL로 직접 업로드하므로 서버가 파일을 받지 않는다");
}
```

- **한 포트에 구현체가 여러 개면 `@ConditionalOnProperty`로 하나만 Bean으로 띄운다**(`@Profile`이 아니라 — 로컬/운영을 나누는 게 아니라 같은 환경 안에서 설정값으로 고르는 것이므로).

```java
@Component
@ConditionalOnProperty(prefix = "file.storage", name = "type", havingValue = "s3")
public class S3FileStorageClient implements FileStorageClient { ... }

@Component
@ConditionalOnProperty(prefix = "file.storage", name = "type", havingValue = "local", matchIfMissing = true)
public class LocalFileStorageClient implements FileStorageClient { ... }
```

- **임시 구현체(추후 다른 구현체로 완전히 교체될 코드)에는 "무엇으로 전환하면 이 코드를 지운다"는 클래스 주석을 남긴다.** 그 임시 구현체에 딸린 전용 엔드포인트·메서드(예: 로컬 전용 업로드 수신 API)도 같은 문구로 표시해서, 실제 전환 작업을 할 때 검색 한 번으로 같이 지울 대상을 찾을 수 있게 한다.

```java
/**
 * 로컬 디스크 기반 임시 구현체. S3 연동 시 이 클래스와 "임시 로컬 업로드 엔드포인트"를 함께 제거한다.
 */
@Component
@ConditionalOnProperty(prefix = "file.storage", name = "type", havingValue = "local", matchIfMissing = true)
public class LocalFileStorageClient implements FileStorageClient { ... }
```

---

## 3. Validation

- Jakarta Bean Validation으로 형식 검증. `api:*`에 `spring-boot-starter-validation` 추가.
- Request record **컴포넌트에 직접** 어노테이션을 붙이고 Controller에서 `@Valid`로 트리거한다(`@field:` 불필요 — Java record).
- 메시지는 한글.

```java
public record MemberRegisterRequest(
    @NotBlank(message = "학번을 입력해 주세요.")
    @Pattern(regexp = "^\\d{8}$", message = "학번은 숫자 8자리여야 합니다.")
    String studentNo,

    @NotBlank(message = "이름을 입력해 주세요.")
    @Size(max = 20, message = "이름은 20자 이하로 입력해 주세요.")
    String name
) {
    public MemberRegisterCommand toCommand() {
        return new MemberRegisterCommand(studentNo, name);
    }
}
```

- 검증 실패 시 `MethodArgumentNotValidException` → `GlobalExceptionHandler`가 `CommonErrorCode.INVALID_INPUT`(400)으로 응답(`error-handling.md` 4절).
- 형식을 넘는 비즈니스 규칙 검증(학번 중복, 재고, 정원 등)은 `{Domain}ServiceImpl`에서 `BusinessException`으로 처리한다.
