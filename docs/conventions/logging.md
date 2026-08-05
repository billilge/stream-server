# Logging — 로깅 컨벤션 (Java 21)

> MDC 기반 요청 추적, 요청/응답 로깅, 로그 출력 설정을 다룬다.
> 로깅 관련 코드 작업, 새 MDC 필드 추가 시 참조한다.

---

## 1. 모듈 위치

로깅은 횡단관심사이므로 `gateway:logging`에서 담당하며 `core:common`(`PrincipalProvider`)에만 의존한다.

```
gateway/logging/
├── MdcFilter.java            — 모든 요청에 requestId, userId를 MDC에 세팅
├── LoggingFilter.java        — 모든 요청에 대한 access log 출력
├── MdcKeys.java              — MDC 키 상수 (REQUEST_ID, USER_ID). GlobalExceptionHandler가 참조
└── config/
    └── LoggingProperties.java — 로깅 제외 경로 설정 바인딩
```

---

## 2. MDC 필드

| 필드 | 설명 | 세팅 시점 |
| --- | --- | --- |
| `requestId` | 요청 고유 UUID | `MdcFilter` |
| `userId` | 인증된 사용자 ID | `MdcFilter` (미인증 시 세팅 안 함) |

- MDC는 요청 스레드에 바인딩되어 처리 중 어디서든 `MDC.get("requestId")`로 꺼낼 수 있다.
- `MdcFilter`는 `finally`에서 `MDC.clear()`를 호출해 스레드 풀 오염을 방지한다.
- MDC 키는 `MdcKeys`(`gateway:logging`) 상수로 관리하고, `api:common-api`의 `GlobalExceptionHandler`가 참조한다. 이 때문에 `api:*`는 `gateway:logging`에 의존한다.

---

## 3. 필터 실행 순서

```
Spring Security Filter (@Order -100)
    ↓
MdcFilter (@Order 0)     — requestId, userId MDC 세팅
    ↓
LoggingFilter (@Order 1) — access log 출력
    ↓
DispatcherServlet
```

- `userId`를 MDC에 넣으려면 인증 결과가 필요하므로 `MdcFilter`는 Security 필터 이후에 위치한다. `PrincipalProvider`(`core:common`)로 userId를 조회해 세팅한다.

---

## 4. 로그 레벨 책임 분리

| 상황 | 담당 | 레벨 |
| --- | --- | --- |
| 모든 요청 (상태 코드 무관) | `LoggingFilter` | `INFO` |
| 비즈니스 예외 (4xx) | `GlobalExceptionHandler` | `WARN` |
| 서버 예외 (5xx) | `GlobalExceptionHandler` | `ERROR` |

---

## 5. 로그 출력 포맷

### local — 사람이 읽기 좋은 콘솔 출력

```
10:32:15.123 INFO  [abc-123,42] ...logging.LoggingFilter - POST /v1/app/rentals 201 (35ms)
```

### 그 외 (dev, prod) — JSON 구조화 로깅

```json
{
  "@timestamp": "2026-07-02T10:32:15.123Z",
  "level": "INFO",
  "requestId": "abc-123",
  "userId": "42",
  "message": "POST /v1/app/rentals 201 (35ms)"
}
```

`LogstashEncoder`가 MDC 필드(`requestId`, `userId`)를 자동 JSON 필드로 포함한다.

---

## 6. 비동기 로깅 (AsyncAppender)

`local` 외 환경에서는 `AsyncAppender`로 로그 쓰기를 별도 스레드에서 처리한다.

| 설정 | 값 | 이유 |
| --- | --- | --- |
| `queueSize` | 1024 | 버스트 트래픽 대응 |
| `discardingThreshold` | 0 | 큐 여유와 무관하게 모든 레벨 유지 |
| `neverBlock` | true | 큐 풀 시 요청 스레드 블로킹 방지 (로그 일부 유실 허용) |

---

## 7. 로깅 제외 경로

`LoggingFilter`는 `gateway.logging.exclude-paths`에 등록된 경로를 로깅하지 않는다. Ant 패턴을 지원하며 `application-gateway-logging.yml`에서 설정한다.

```yaml
gateway:
  logging:
    exclude-paths:
      - /actuator/**
      - /swagger-ui/**
      - /swagger-ui.html
      - /v3/api-docs/**
```

헬스체크·Swagger 등 주기적·내부 호출 노이즈를 방지한다.
