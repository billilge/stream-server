package kr.ac.kookmin.stream.security;

import java.util.Set;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.common.CouncilDepartment;
import kr.ac.kookmin.stream.common.PrincipalProvider;
import kr.ac.kookmin.stream.common.Role;
import kr.ac.kookmin.stream.security.jwt.UserAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityPrincipalProvider implements PrincipalProvider {

    @Override
    public Long userId() {
        return currentAuthentication().userId();
    }

    @Override
    public Set<Role> roles() {
        return currentAuthentication().roles();
    }

    @Override
    public Set<CouncilDepartment> councilDepartments() {
        return currentAuthentication().councilDepartments();
    }

    private UserAuthentication currentAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof UserAuthentication userAuthentication)) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        }
        return userAuthentication;
    }
}
