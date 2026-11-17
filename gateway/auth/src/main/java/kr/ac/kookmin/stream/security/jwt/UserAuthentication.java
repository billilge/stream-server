package kr.ac.kookmin.stream.security.jwt;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;
import kr.ac.kookmin.stream.common.CouncilDepartment;
import kr.ac.kookmin.stream.common.Role;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@Accessors(fluent = true)
public class UserAuthentication extends AbstractAuthenticationToken {

    private static final String COUNCIL_AUTHORITY_PREFIX = "COUNCIL_";

    private final Long userId;
    private final Set<Role> roles;
    private final Set<CouncilDepartment> councilDepartments;
    // 검증을 마친 JWT 원문. Bearer 파싱은 JwtAuthFilter 한 곳에서만 하고, 다른 모듈은 PrincipalProvider로 꺼낸다
    private final String accessToken;

    private UserAuthentication(JwtPayload payload, String accessToken) {
        super(toAuthorities(payload));
        this.userId = payload.userId();
        this.roles = payload.roles();
        this.councilDepartments = payload.councilDepartments();
        this.accessToken = accessToken;
        setAuthenticated(true);
    }

    public static UserAuthentication of(JwtPayload payload, String accessToken) {
        return new UserAuthentication(payload, accessToken);
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    private static Collection<GrantedAuthority> toAuthorities(JwtPayload payload) {
        return Stream.concat(
                payload.roles().stream().map(Role::name),
                payload.councilDepartments().stream().map(department -> COUNCIL_AUTHORITY_PREFIX + department.name())
            )
            .<GrantedAuthority>map(SimpleGrantedAuthority::new)
            .toList();
    }
}
