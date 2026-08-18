package kr.ac.kookmin.stream.security.config;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * 인증 없이 여는 엔드포인트. SecurityConfig의 permitAll 대상이며 용도별로 묶어 관리한다.
 */
@Getter
@Accessors(fluent = true)
public enum PublicEndpoints {

    HEALTH_CHECK(List.of(
        "/actuator/health"
    )),
    SWAGGER(List.of(
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/v3/api-docs/**"
    ));

    private final List<String> patterns;
    private final List<PathPattern> pathPatterns;

    PublicEndpoints(List<String> patterns) {
        PathPatternParser parser = new PathPatternParser();
        this.patterns = patterns;
        this.pathPatterns = patterns.stream().map(parser::parse).toList();
    }

    public static String[] allPatterns() {
        return Arrays.stream(values())
            .flatMap(endpoints -> endpoints.patterns.stream())
            .toArray(String[]::new);
    }

    public static boolean isPublic(String path) {
        PathContainer pathContainer = PathContainer.parsePath(path);
        return Arrays.stream(values())
            .flatMap(endpoints -> endpoints.pathPatterns.stream())
            .anyMatch(pathPattern -> pathPattern.matches(pathContainer));
    }
}
