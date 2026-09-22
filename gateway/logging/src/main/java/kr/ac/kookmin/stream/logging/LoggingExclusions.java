package kr.ac.kookmin.stream.logging;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 로깅 필터가 건너뛸 요청 판별. 헬스체크는 빈번히 폴링되므로 MDC·액세스 로그 대상에서 제외한다.
 * 경로는 액추에이터 기본값이자 security 모듈 {@code PublicEndpoints.HEALTH_CHECK}와 같은 값이다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class LoggingExclusions {

    private static final String HEALTH_CHECK_PATH = "/actuator/health";

    static boolean isExcluded(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals(HEALTH_CHECK_PATH) || path.startsWith(HEALTH_CHECK_PATH + "/");
    }
}
