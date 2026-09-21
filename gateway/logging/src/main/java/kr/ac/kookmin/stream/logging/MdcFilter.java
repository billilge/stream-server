package kr.ac.kookmin.stream.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 요청마다 requestId(UUID)를 발급해 MDC에 넣고, 응답이 끝나면 MDC 전체를 정리한다.
 * 인증 거부 로그에도 requestId가 남도록 Spring Security 필터 체인(기본 order -100)보다 앞에 둔다.
 */
@Component
@Order(-120)
public class MdcFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        MDC.put(MdcKeys.REQUEST_ID, UUID.randomUUID().toString());
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
