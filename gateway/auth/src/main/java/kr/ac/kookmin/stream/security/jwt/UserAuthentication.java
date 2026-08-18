package kr.ac.kookmin.stream.security.jwt;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import kr.ac.kookmin.stream.common.CouncilDepartment;
import kr.ac.kookmin.stream.common.Role;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class UserAuthentication extends AbstractAuthenticationToken {

    private static final String COUNCIL_AUTHORITY_PREFIX = "COUNCIL_";

    private final Long userId;
    private final Set<Role> roles;
    private final Set<CouncilDepartment> councilDepartments;

    public UserAuthentication(JwtPayload payload) {
        super(toAuthorities(payload));
        this.userId = payload.userId();
        this.roles = payload.roles();
        this.councilDepartments = payload.councilDepartments();
        setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    public Long userId() {
        return userId;
    }

    public Set<Role> roles() {
        return roles;
    }

    public Set<CouncilDepartment> councilDepartments() {
        return councilDepartments;
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
