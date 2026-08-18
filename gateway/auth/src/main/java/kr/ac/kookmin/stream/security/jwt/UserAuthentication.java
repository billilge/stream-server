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

    private UserAuthentication(JwtPayload payload) {
        super(toAuthorities(payload));
        this.userId = payload.userId();
        this.roles = payload.roles();
        this.councilDepartments = payload.councilDepartments();
        setAuthenticated(true);
    }

    public static UserAuthentication from(JwtPayload payload) {
        return new UserAuthentication(payload);
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
