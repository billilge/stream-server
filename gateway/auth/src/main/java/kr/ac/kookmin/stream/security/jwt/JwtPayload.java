package kr.ac.kookmin.stream.security.jwt;

import java.util.Set;
import kr.ac.kookmin.stream.common.CouncilDepartment;
import kr.ac.kookmin.stream.common.Role;

public record JwtPayload(
    Long userId,
    Set<Role> roles,
    Set<CouncilDepartment> councilDepartments
) {}
