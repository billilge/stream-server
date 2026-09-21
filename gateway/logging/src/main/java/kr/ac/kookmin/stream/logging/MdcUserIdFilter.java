package kr.ac.kookmin.stream.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.PrincipalProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 인증이 끝난 뒤 현재 사용자 id를 MDC에 넣는다. MDC 정리는 {@link MdcFilter}가 담당한다.
 * Spring Security 필터 체인(기본 order -100)에서 인증이 완료된 뒤 실행되도록 그 뒤(큰 값)에 둔다.
 */
@Component
@Order(-80)
@RequiredArgsConstructor
public class MdcUserIdFilter extends OncePerRequestFilter {

    private final PrincipalProvider principalProvider;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        putUserId();
        filterChain.doFilter(request, response);
    }

    private void putUserId() {
        try {
            MDC.put(MdcKeys.USER_ID, String.valueOf(principalProvider.userId()));
        } catch (BusinessException ignored) {
            // 미인증 요청(공개 엔드포인트 등)은 userId 없이 진행한다
        }
    }
}
