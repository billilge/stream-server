# Error Handling — 예외 처리 & Swagger 에러 문서화 (Java 21)

> ErrorCode/BusinessException 체계, GlobalExceptionHandler, `@ApiErrorCode` 기반 Swagger 에러 문서화를 다룬다.
> 새 에러 코드 추가, 예외 처리 로직 작업 시 참조한다. 모듈 배치는 `architecture.md`.

---

## 1. ErrorCode 인터페이스 & ErrorStatus

`ErrorCode` 인터페이스와 HTTP 상태 코드 상수(`ErrorStatus`)는 모두 `core:common`에 둔다. Spring의 `HttpStatus`를 직접 쓰지 않아 프레임워크 의존을 제거한다.

```java
// core:common — ErrorCode.java
public interface ErrorCode {
    String name();     // enum 상수는 java.lang.Enum.name()으로 자동 충족
    int status();
    String message();
}
```

```java
// core:common — ErrorStatus.java
public final class ErrorStatus {
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int UNAUTHORIZED = 401;
    public static final int NOT_FOUND = 404;
    public static final int BAD_REQUEST = 400;
    public static final int CONFLICT = 409;
    public static final int FORBIDDEN = 403;
    private ErrorStatus() {}
}
```

---

## 2. CommonErrorCode & 도메인 ErrorCode

- 공통 에러 코드는 `core:common`의 `CommonErrorCode`.
- 도메인 에러 코드는 각 `core:domain:{도메인}`에 별도 enum으로 두고, api Swagger 문서화가 참조하므로 **공개(최상위)** 에 둔다.

```java
// core:common — CommonErrorCode.java
public enum CommonErrorCode implements ErrorCode {

    INVALID_INPUT(ErrorStatus.BAD_REQUEST, "유효하지 않은 입력값입니다."),
    UNAUTHORIZED(ErrorStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(ErrorStatus.FORBIDDEN, "접근 권한이 없습니다."),
    INTERNAL_SERVER_ERROR(ErrorStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류입니다. 다시 시도해 주세요.");

    private final int status;
    private final String message;

    CommonErrorCode(int status, String message) { this.status = status; this.message = message; }

    @Override public int status() { return status; }
    @Override public String message() { return message; }
}
```

```java
// core:domain:rental — RentalErrorCode.java (공개)
public enum RentalErrorCode implements ErrorCode {

    RENTAL_NOT_FOUND(ErrorStatus.NOT_FOUND, "대여 신청을 찾을 수 없습니다."),
    ITEM_OUT_OF_STOCK(ErrorStatus.CONFLICT, "대여 가능한 재고가 없습니다.");

    private final int status;
    private final String message;

    RentalErrorCode(int status, String message) { this.status = status; this.message = message; }

    @Override public int status() { return status; }
    @Override public String message() { return message; }
}
```

---

## 3. BusinessException 계층

`core:common`에 둔다.

```java
// core:common — BusinessException.java
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, Object... formatArgs) {
        super(formatArgs.length == 0 ? errorCode.message() : errorCode.message().formatted(formatArgs));
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() { return errorCode; }
}
```

- 대부분 `ErrorCode`를 넘겨 직접 던진다. 추가 필드가 필요할 때만 상속한다.

```java
throw new BusinessException(RentalErrorCode.ITEM_OUT_OF_STOCK);
```

---

## 4. GlobalExceptionHandler

`api:common-api`에 둔다. MDC에서 `requestId`, `userId`를 읽어 로그에 포함한다(MDC 키 상수는 `gateway:logging`, `logging.md` 참조). `BusinessException`·`@Valid` 예외는 `warn`, 그 외는 `error`.

```java
// api:common-api — GlobalExceptionHandler.java
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException e) {
        log.warn("[requestId={}, userId={}] ({}) {}",
            MDC.get(REQUEST_ID), MDC.get(USER_ID), e.getErrorCode().name(), e.getMessage());
        return ResponseEntity.status(e.getErrorCode().status()).body(ApiResponse.error(e));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .findFirst().map(FieldError::getDefaultMessage)
            .orElse(CommonErrorCode.INVALID_INPUT.message());
        log.warn("[requestId={}, userId={}] (INVALID_INPUT) {}",
            MDC.get(REQUEST_ID), MDC.get(USER_ID), message);
        return ResponseEntity.status(CommonErrorCode.INVALID_INPUT.status())
            .body(ApiResponse.error(CommonErrorCode.INVALID_INPUT.name(), message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleInternal(Exception e) {
        log.error("[requestId={}, userId={}] {}",
            MDC.get(REQUEST_ID), MDC.get(USER_ID), e.getMessage(), e);
        return ResponseEntity.status(ErrorStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(CommonErrorCode.INTERNAL_SERVER_ERROR));
    }
}
```

---

## 5. ApiResponse 에러 팩토리

`ApiResponse`(`api:common-api`)에 정적 팩토리를 둔다. `BusinessException`, `ErrorCode`, `(code, message)`를 받는다.

```java
public static <T> ApiResponse<T> error(BusinessException e) {
    return new ApiResponse<>(false, e.getErrorCode().name(), e.getMessage(), null);
}
public static <T> ApiResponse<T> error(ErrorCode errorCode) {
    return new ApiResponse<>(false, errorCode.name(), errorCode.message(), null);
}
public static <T> ApiResponse<T> error(String code, String message) {
    return new ApiResponse<>(false, code, message, null);
}
```

---

## 6. Swagger 에러 문서화

`@ApiErrorCode`(`api:common-api`)로 API가 실제 던지는 개별 코드만 문서화한다. `type`으로 enum 클래스를, `codes`로 상수 이름을 넘긴다. 한 어노테이션에는 같은 `type`만 담고, 다른 enum은 어노테이션을 추가로 붙인다.

> Java `@Repeatable`은 컨테이너 어노테이션을 명시해야 한다(Kotlin과 다른 점).

```java
// api:common-api
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ApiErrorCodes.class)
public @interface ApiErrorCode {
    Class<? extends ErrorCode> type();
    String[] codes();
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiErrorCodes {
    ApiErrorCode[] value();
}
```

```java
@ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
@ApiErrorCode(type = RentalErrorCode.class, codes = {"RENTAL_NOT_FOUND", "ITEM_OUT_OF_STOCK"})
@PostMapping("/v1/app/rentals")
public ApiResponse<RentalApplyResponse> apply(...) { ... }
```

- `codes`는 런타임에 enum 상수와 대조해 검증한다. 없는 이름이면 문서 조립 시 즉시 실패해 오탈자를 잡는다.
- `ApiErrorCodeCustomizer`(`OperationCustomizer`, `@Component`)가 `codes`를 상수로 resolve해 OpenAPI 응답에 추가한다. springdoc이 자동 감지한다.
- 같은 HTTP status의 코드가 여러 개면 **같은 응답에 example을 여러 개** 붙인다(그룹핑하지 않으면 덮인다). 기존 응답이 있으면 example만 병합한다.
- `io.swagger...responses.ApiResponse`가 `dto.ApiResponse`와 충돌하면 정규명(FQCN)으로 구분한다.
