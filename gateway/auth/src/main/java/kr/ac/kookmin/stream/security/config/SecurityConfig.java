package kr.ac.kookmin.stream.security.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.common.ErrorCode;
import kr.ac.kookmin.stream.common.Role;
import kr.ac.kookmin.stream.security.jwt.JwtAuthFilter;
import kr.ac.kookmin.stream.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

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

    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver handlerExceptionResolver;

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
                .authenticationEntryPoint((req, res, e) -> delegate(req, res, CommonErrorCode.UNAUTHORIZED))
                .accessDeniedHandler((req, res, e) -> delegate(req, res, CommonErrorCode.FORBIDDEN)))
            .addFilterBefore(
                JwtAuthFilter.of(jwtProvider, handlerExceptionResolver),
                UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    private void delegate(HttpServletRequest request, HttpServletResponse response, ErrorCode errorCode) {
        handlerExceptionResolver.resolveException(request, response, null, new BusinessException(errorCode));
    }
}
