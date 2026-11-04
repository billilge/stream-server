package kr.ac.kookmin.stream.auth.domain.admin.domain;

// admin_config_values에 저장되는 운영진 인증 관련 키. 공용 비밀번호는 평문이 아니라 BCrypt 해시로만 저장한다.
public final class AdminAuthConfigKeys {

    public static final String PASSWORD_HASH = "ADMIN_PASSWORD_HASH";

    private AdminAuthConfigKeys() {}
}
