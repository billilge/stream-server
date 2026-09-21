package kr.ac.kookmin.stream.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 요청 완료 시 method·path·status·소요시간을 한 줄로 남기는 액세스 로그.
 * MDC(requestId/userId)가 채워진 상태에서 기록하도록 {@link MdcFilter} 안쪽, 시큐리티 앞에 둔다.
 */
@Component
@Order(-110)
public class AccessLogFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AccessLogFilter.class);

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            log.info("[requestId={}, userId={}] {} {} {} ({}ms)",
                MDC.get(MdcKeys.REQUEST_ID), MDC.get(MdcKeys.USER_ID),
                request.getMethod(), request.getRequestURI(), response.getStatus(),
                System.currentTimeMillis() - start);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return LoggingExclusions.isExcluded(request);
    }
}
