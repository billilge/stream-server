package kr.ac.kookmin.stream.member.domain.member.domain;

import java.util.Objects;
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
    // 로그인 provider가 주는 학적 상태 원문(재학·휴학·졸업 등). 로그인 전 이관 회원은 null이다
    private String academicStatus;
    private String email;
    // 회원가입 때 받는 휴대전화 번호(숫자만). 가입 전 회원과 이관 회원은 null이다
    private String phoneNumber;
    private String fcmToken;
    private Role role;
    private CouncilDepartment councilDepartment;

    // 로그인으로 처음 가입하는 회원. 운영진 권한·학생회 부서는 가입 뒤 따로 부여한다
    public static Member create(String studentId, String name, Department department, String academicStatus) {
        return new Member(null, studentId, name, department, academicStatus, null, null, null, Role.STUDENT, null);
    }

    public static Member of(
        Long id,
        String studentId,
        String name,
        Department department,
        String academicStatus,
        String email,
        String phoneNumber,
        String fcmToken,
        Role role,
        CouncilDepartment councilDepartment
    ) {
        return new Member(
            id, studentId, name, department, academicStatus, email, phoneNumber, fcmToken, role, councilDepartment);
    }

    // 로그인할 때마다 provider의 최신 이름·학부·학적 상태로 갱신한다. 바뀐 값이 있으면 true를 돌려준다
    public boolean updateProfile(String name, Department department, String academicStatus) {
        if (Objects.equals(this.name, name)
            && this.department == department
            && Objects.equals(this.academicStatus, academicStatus)) {
            return false;
        }
        this.name = name;
        this.department = department;
        this.academicStatus = academicStatus;
        return true;
    }

    public void registerPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean hasPhoneNumber() {
        return phoneNumber != null;
    }
}
