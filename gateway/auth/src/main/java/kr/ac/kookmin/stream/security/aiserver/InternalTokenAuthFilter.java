package kr.ac.kookmin.stream.security.aiserver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * {@code X-Internal-Token}이 설정값과 같으면 AI 서버로 인증한다. 다르면 아무것도 하지 않는다 —
 * 인증이 비어 있으면 뒤의 인가 단계가 401로 막는다.
 * <p>
 * 빈으로 등록하지 않는다({@code JwtAuthFilter}와 같은 이유). 빈이 되면 스프링 부트가 모든 요청의 서블릿 필터로도 걸어 버린다.
 * {@code AiServerSecurityConfig}가 {@code /v1/ai/**} 체인에만 넣는다.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class InternalTokenAuthFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-Internal-Token";

    private final AiServerProperties properties;

    public static InternalTokenAuthFilter of(AiServerProperties properties) {
        return new InternalTokenAuthFilter(properties);
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        if (matches(request.getHeader(HEADER))) {
            SecurityContextHolder.getContext().setAuthentication(AiServerAuthentication.create());
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 설정값이 비어 있으면 무엇이 와도 거절한다.
     * <p>
     * {@code equals} 대신 {@link MessageDigest#isEqual}을 쓴다. {@code equals}는 앞에서부터 비교하다 다르면 바로 멈춰서,
     * 응답 시간 차이로 토큰을 한 글자씩 알아낼 수 있다. {@code isEqual}은 길이가 같으면 항상 끝까지 비교한다.
     */
    private boolean matches(String token) {
        if (!properties.hasToken() || token == null) {
            return false;
        }
        return MessageDigest.isEqual(
            token.getBytes(StandardCharsets.UTF_8),
            properties.internalToken().getBytes(StandardCharsets.UTF_8)
        );
    }
}
