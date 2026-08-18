package kr.ac.kookmin.stream.security.config;

import kr.ac.kookmin.stream.common.Role;
import kr.ac.kookmin.stream.security.handler.RestAccessDeniedHandler;
import kr.ac.kookmin.stream.security.handler.RestAuthenticationEntryPoint;
import kr.ac.kookmin.stream.security.jwt.JwtAuthFilter;
import kr.ac.kookmin.stream.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] PERMIT_ALL_PATHS = {
        "/actuator/health",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/v3/api-docs/**"
    };

    private final JwtProvider jwtProvider;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(request -> request
                .requestMatchers(PERMIT_ALL_PATHS).permitAll()
                .requestMatchers("/v1/admin/**").hasAuthority(Role.ADMIN.name())
                .requestMatchers("/v1/app/**").hasAuthority(Role.STUDENT.name())
                .anyRequest().authenticated())
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler))
            // ExceptionTranslationFilter 뒤에 두어야 필터가 던진 인증 예외가 EntryPoint로 넘어간다
            .addFilterBefore(JwtAuthFilter.of(jwtProvider), AuthorizationFilter.class)
            .build();
    }
}
