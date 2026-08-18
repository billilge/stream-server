package kr.ac.kookmin.stream.common;

import java.util.Set;

public interface PrincipalProvider {
    Long userId();
    Set<Role> roles();
    Set<CouncilDepartment> councilDepartments();
}
