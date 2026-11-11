package kr.ac.kookmin.stream.security.config;

import kr.ac.kookmin.stream.security.aiserver.AiServerAuthentication;
import kr.ac.kookmin.stream.security.aiserver.AiServerProperties;
import kr.ac.kookmin.stream.security.aiserver.InternalTokenAuthFilter;
import kr.ac.kookmin.stream.security.handler.RestAccessDeniedHandler;
import kr.ac.kookmin.stream.security.handler.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

/**
 * AI 서버 전용 경로 {@code /v1/ai/**}의 보안 체인. 학생·운영진 체인({@link SecurityConfig})은 건드리지 않고 옆에 둔다.
 * <p>
 * {@code @Order(1)}로 기존 체인보다 먼저 검사한다. {@code securityMatcher}에 걸린 요청은 이 체인만 타고,
 * 나머지는 기존 체인으로 넘어간다. 그래서 학생 JWT로는 {@code /v1/ai/**}에 들어올 수 없고,
 * 내부 토큰으로는 {@code /v1/app/**}에 들어갈 수 없다.
 * <p>
 * CORS는 열지 않는다. 브라우저가 아니라 같은 네트워크의 AI 서버만 부른다.
 */
@Configuration
@RequiredArgsConstructor
public class AiServerSecurityConfig {

    private static final String AI_SERVER_PATH = "/v1/ai/**";

    private final AiServerProperties aiServerProperties;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    @Bean
    @Order(1)
    public SecurityFilterChain aiServerSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
            .securityMatcher(AI_SERVER_PATH)
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(request -> request
                .anyRequest().hasAuthority(AiServerAuthentication.AUTHORITY))
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler))
            // ExceptionTranslationFilter 뒤에 두어야 인증 실패가 EntryPoint(401)로 넘어간다 — SecurityConfig와 같은 위치
            .addFilterBefore(InternalTokenAuthFilter.of(aiServerProperties), AuthorizationFilter.class)
            .build();
    }
}
