package kr.ac.kookmin.stream.member.domain.member.domain;

import kr.ac.kookmin.stream.common.CouncilDepartment;
import kr.ac.kookmin.stream.common.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member {

    private Long id;
    private String studentId;
    private String name;
    private Department department;
    private String email;
    private String fcmToken;
    private Role role;
    private CouncilDepartment councilDepartment;

    public static Member of(
        Long id,
        String studentId,
        String name,
        Department department,
        String email,
        String fcmToken,
        Role role,
        CouncilDepartment councilDepartment
    ) {
        return new Member(id, studentId, name, department, email, fcmToken, role, councilDepartment);
    }
}
