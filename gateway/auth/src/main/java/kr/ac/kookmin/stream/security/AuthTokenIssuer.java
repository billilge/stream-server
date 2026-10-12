package kr.ac.kookmin.stream.security;

import java.util.EnumSet;
import java.util.Set;
import kr.ac.kookmin.stream.common.CouncilDepartment;
import kr.ac.kookmin.stream.common.Role;
import kr.ac.kookmin.stream.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 로그인 API가 서비스 토큰을 발급할 때 쓰는 진입점.
 * JwtProvider는 Modulith가 내부로 보는 security.jwt 패키지에 있어 api 모듈이 직접 부를 수 없으므로 여기서 감싼다.
 */
@Component
@RequiredArgsConstructor
public class AuthTokenIssuer {

    private final JwtProvider jwtProvider;

    public String issue(Long memberId, Role role, CouncilDepartment councilDepartment) {
        return jwtProvider.generateAccessToken(memberId, rolesOf(role), councilDepartmentsOf(role, councilDepartment));
    }

    // 운영진도 학생 앱(/v1/app/**, STUDENT)을 쓰므로 ADMIN에게는 STUDENT를 함께 준다
    private Set<Role> rolesOf(Role role) {
        return role == Role.ADMIN ? EnumSet.of(Role.ADMIN, Role.STUDENT) : EnumSet.of(role);
    }

    // 학생회 부서 권한은 ADMIN에게만 싣는다
    private Set<CouncilDepartment> councilDepartmentsOf(Role role, CouncilDepartment councilDepartment) {
        if (role != Role.ADMIN || councilDepartment == null) {
            return EnumSet.noneOf(CouncilDepartment.class);
        }
        return EnumSet.of(councilDepartment);
    }
}
