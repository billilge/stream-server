package kr.ac.kookmin.stream.security.aiserver;

import java.util.List;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * 내부 토큰을 확인한 AI 서버 요청. 학생도 운영진도 아니라 회원 ID가 없다.
 * <p>
 * 권한 {@link #AUTHORITY}는 {@code Role} enum에 넣지 않는다. 넣으면 JWT·{@code PrincipalProvider}까지
 * AI 서버를 알아야 하는데, AI 서버는 {@code /v1/ai/**}에서만 쓰이는 별도 클라이언트다.
 */
public class AiServerAuthentication extends AbstractAuthenticationToken {

    public static final String AUTHORITY = "AI_SERVER";

    private AiServerAuthentication() {
        super(List.of(new SimpleGrantedAuthority(AUTHORITY)));
        setAuthenticated(true);
    }

    public static AiServerAuthentication create() {
        return new AiServerAuthentication();
    }

    @Override
    public Object getPrincipal() {
        return AUTHORITY;
    }

    @Override
    public Object getCredentials() {
        return null;
    }
}
