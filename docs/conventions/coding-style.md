# Coding Style — 네이밍 & 클래스 작성 규칙 (Java 21)

> 이 문서는 클래스 네이밍, 도메인/DTO/Command/Entity/Repository/Service/UseCase 작성 패턴, Validation 규칙을 다룬다.
> 실제 코드 생성/리뷰 시 참조한다. 모듈 배치·레이어 규칙은 `architecture.md`, 예외는 `error-handling.md`를 함께 본다.
> 예시 도메인은 `member`(학생 회원, `core:domain:member`)를 사용한다.

---

## 1. 네이밍 컨벤션

Business는 공개 인터페이스 + `internal` 구현체로 구성한다.

| 레이어 | 역할 | 네이밍 | 위치 |
| --- | --- | --- | --- |
| Presentation | HTTP 요청 처리 | `{Client}{Domain}Controller` (예: `AppMemberController`, `AdminMemberController`) | `api:{client}-api` |
| Presentation | 요청 객체 | `{Domain}{Action}Request` | `api:{client}-api` |
| Presentation | 응답 객체 | `{Domain}{Action}Response` | `api:{client}-api` |
| Presentation | 교차 도메인 조합 | `{Feature}UseCase` | `api:{client}-api` |
| Business | 공개 진입점 인터페이스 | `{Domain}Service` | `core:domain:{도메인}` (최상위) |
| Business | 구현체 | `{Domain}ServiceImpl` | `core:domain:{도메인}` (`internal`) |
| Data Access | Repository 인터페이스 | `{Domain}Repository` | `core:domain:{도메인}` (최상위, 공개) |
| Data Access | Repository 구현체 | `{Domain}RepositoryImpl` | `infrastructure:db` |
| Data Access | JPA Repository | `{Domain}JpaRepository` | `infrastructure:db` |
| Data Access | 외부 API 클라이언트 인터페이스 | `{Domain}Client` | `core:domain:{도메인}` (최상위, 공개) |
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

- Request/Response DTO는 `record`(`api:{client}-api`).
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

```java
// core:common
public record CursorSliceResult<T>(List<T> content, boolean hasNext, Long nextCursor) {}
public record PageResult<T>(List<T> content, int page, int size, long totalCount, int totalPage) {}
```

### 2-5. JPA Entity

- `infrastructure:db`에 선언한다. 도메인 객체를 받는 생성자와 `toDomain()`을 제공하고, 용도에 맞는 Base Entity를 상속한다. JPA용 `protected` 기본 생성자를 둔다.

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
public class MemberJpaEntity extends BaseSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentNo;
    private String name;

    protected MemberJpaEntity() {}   // JPA

    public MemberJpaEntity(Member member) {
        this.id = member.id();
        this.studentNo = member.studentNo();
        this.name = member.name();
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
// core:domain:member (공개)
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
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    public MemberRepositoryImpl(MemberJpaRepository memberJpaRepository) {
        this.memberJpaRepository = memberJpaRepository;
    }

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
        return memberJpaRepository.save(new MemberJpaEntity(member)).toDomain();
    }
}
```

### 2-7. Business Layer (Service)

- `{Domain}Service`는 공개 인터페이스(최상위), `{Domain}ServiceImpl`는 구현체(`internal`). 외부에 숨기기 위해 **package-private 클래스**로 선언한다(package-private `@Service`도 빈 등록됨).
- **`{Domain}ServiceImpl`가 `{Domain}Repository`를 직접 참조**한다.
- 비즈니스 규칙 검증은 `{Domain}ServiceImpl`(또는 internal 협력 객체)에서 하고 `BusinessException`을 던진다.
- 트랜잭션 경계는 Service 메서드에. 조회 전용은 `@Transactional(readOnly = true)`, 교차 도메인 UseCase가 감쌀 수 있게 **기본 전파(REQUIRED)** 를 쓴다(`architecture.md` 6-1절).

```java
// core:domain:member (최상위)
public interface MemberService {
    Member register(MemberRegisterCommand command);
    Member getById(Long id);
}
```

```java
// core:domain:member/internal (감춰짐)
@Service
class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

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
- 클라이언트 접두사(`Admin`/`App`)로 컨트롤러를 구분하고, 각 클라이언트 모듈의 팀 패키지에 둔다(`architecture.md` 2-2절).

```java
// api:admin-api — 운영진 회원 등록
@RestController
@RequestMapping("/v1/admin/members")
public class AdminMemberController {

    private final MemberService memberService;

    public AdminMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

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
public class AppMemberController {

    private final MemberService memberService;

    public AppMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/me")
    public ApiResponse<MemberResponse> me(StudentApiUser apiUser) {
        Member member = memberService.getById(apiUser.userId());
        return ApiResponse.success(MemberResponse.from(member));
    }
}
```

### 2-9. UseCase (교차 도메인 조합)

- **서로 다른 도메인의 `{Domain}Service` 2개 이상**을 조합할 때만 만든다. `api:{client}-api`에 두고 공개 `{Domain}Service`만 조합한다.
- 트랜잭션은 원자성이 필요한 흐름에만. 조회 조합은 걸지 않거나 `readOnly`, 원자적 쓰기에만 `@Transactional`(`architecture.md` 6-1절).

```java
// api:admin-api — 조회 조합 (운영진 대시보드)
@Component
public class AdminDashboardUseCase {

    private final MemberService memberService;
    private final EventService eventService;
    private final NoticeService noticeService;

    public AdminDashboardUseCase(MemberService memberService, EventService eventService, NoticeService noticeService) {
        this.memberService = memberService;
        this.eventService = eventService;
        this.noticeService = noticeService;
    }

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
public class EventApplicationUseCase {

    private final EventService eventService;
    private final FeeService feeService;

    public EventApplicationUseCase(EventService eventService, FeeService feeService) {
        this.eventService = eventService;
        this.feeService = feeService;
    }

    @Transactional  // 정원 차감 + 회비 반영을 원자적으로
    public EventApplicationResponse apply(Long memberId, EventApplyCommand command) {
        Event event = eventService.apply(memberId, command);      // 정원 차감·신청 등록
        feeService.charge(memberId, event.participationFee());     // 참가비 반영
        return EventApplicationResponse.from(event);
    }
}
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
