package kr.ac.kookmin.stream.security.jwt;

import org.springframework.security.core.AuthenticationException;

/**
 * 토큰이 만료됐거나 서명·형식이 올바르지 않을 때. ExceptionTranslationFilter가 잡아
 * SecurityConfig에 설정된 AuthenticationEntryPoint로 넘긴다.
 */
public class InvalidTokenException extends AuthenticationException {

    public InvalidTokenException(Throwable cause) {
        super("유효하지 않은 토큰입니다.", cause);
    }
}
