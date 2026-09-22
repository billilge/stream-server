package kr.ac.kookmin.stream.logging;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 로깅 필터가 건너뛸 요청 판별. 헬스체크는 빈번히 폴링되므로 MDC·액세스 로그 대상에서 제외한다.
 * 경로는 액추에이터 기본값이자 security 모듈 {@code PublicEndpoints.HEALTH_CHECK}와 같은 값이다.
 * 또한 브라우저·크롤러·스캐너가 상시 찔러보는 노이즈 경로도 제외한다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class LoggingExclusions {

    private static final String HEALTH_CHECK_PATH = "/actuator/health";

    // 실존 엔드포인트가 아니지만 브라우저·크롤러·봇 스캐너가 상시 요청해 401 노이즈만 남기는 경로
    private static final Set<String> NOISE_PATHS = Set.of("/", "/robots.txt", "/favicon.ico");

    static boolean isExcluded(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals(HEALTH_CHECK_PATH) || path.startsWith(HEALTH_CHECK_PATH + "/")
            || NOISE_PATHS.contains(path);
    }
}
