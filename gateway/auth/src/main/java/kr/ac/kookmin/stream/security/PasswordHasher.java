package kr.ac.kookmin.stream.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 비밀번호를 BCrypt로 해시하고 비교한다.
 * BCrypt 구현(spring-security-crypto)은 gateway:auth에만 있어, api 모듈이 Security 타입을 몰라도 쓰도록 여기서 감싼다.
 */
@Component
public class PasswordHasher {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}
