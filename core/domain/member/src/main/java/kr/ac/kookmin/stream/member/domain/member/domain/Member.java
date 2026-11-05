package kr.ac.kookmin.stream.member.domain.member.domain;

import java.util.Objects;
import kr.ac.kookmin.stream.common.BusinessException;
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
    // 회원가입 때 인증한 이메일(소문자). 이관 회원은 이전 시스템의 값 그대로다
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

    public void registerEmail(String email) {
        this.email = email;
    }

    public boolean hasEmail() {
        return email != null;
    }

    // 학생회 부서가 있는 운영진. 학생회 부서는 ADMIN에게만 의미가 있어, 운영진에서 내려가 부서 값만 남은 회원은 아니다
    public boolean isCouncilMember() {
        return role == Role.ADMIN && councilDepartment != null;
    }

    /**
     * 운영진 콘솔의 관리자 관리 명단에 올라 있는지. 권한만 잠시 학생으로 내려둔 학생회 인원이 명단에서 사라지지
     * 않도록, 부서가 남아 있으면 운영진이 아니어도 명단으로 본다. 권한·부서를 모두 비워야 명단에서 빠진다.
     */
    public boolean isCouncilRegistered() {
        return role == Role.ADMIN || councilDepartment != null;
    }

    /**
     * 운영진으로 지정한다. 학생회 부서는 반드시 받는다 — 부서가 없는 운영진은 모든 부서 권한을 가진 것으로
     * 간주되므로({@code RequireDepartmentAspect}) 지정 경로에서 빈 부서를 허용하지 않는다.
     */
    public void assignCouncil(CouncilDepartment councilDepartment) {
        validateCouncilDepartmentPresent(Role.ADMIN, councilDepartment);
        this.role = Role.ADMIN;
        this.councilDepartment = councilDepartment;
    }

    /**
     * 권한을 바꾸고, 부서를 받았으면 부서도 바꾼다. 부서가 {@code null}이면 지금 부서를 그대로 둔다 — 권한만
     * 일괄로 바꾸는 경로는 부서를 보내지 않는다.
     * <p>
     * 이 길로는 부서를 비울 수 없다. 비우면 명단에서 빠지는데, 그건 {@link #releaseCouncil()}이 맡는다.
     */
    public void changeCouncil(Role role, CouncilDepartment councilDepartment) {
        CouncilDepartment target = councilDepartment == null ? this.councilDepartment : councilDepartment;
        validateCouncilDepartmentPresent(role, target);
        this.role = role;
        this.councilDepartment = target;
    }

    /** 명단에서 뺀다. 권한을 학생으로 되돌리고 학생회 부서를 비운다. */
    public void releaseCouncil() {
        this.role = Role.STUDENT;
        this.councilDepartment = null;
    }

    /** 회장단 운영진인지. 마지막 회장단이 사라지는 변경을 막을 때 쓴다. */
    public boolean isPresidencyAdmin() {
        return role == Role.ADMIN && councilDepartment == CouncilDepartment.PRESIDENCY;
    }

    private static void validateCouncilDepartmentPresent(Role role, CouncilDepartment councilDepartment) {
        if (role == Role.ADMIN && councilDepartment == null) {
            throw new BusinessException(MemberErrorCode.COUNCIL_DEPARTMENT_REQUIRED);
        }
    }

    // 회원이 바꾼 전화번호로 갱신한다. 바뀐 값이 있으면 true를 돌려준다
    public boolean updatePhoneNumber(String phoneNumber) {
        if (Objects.equals(this.phoneNumber, phoneNumber)) {
            return false;
        }
        this.phoneNumber = phoneNumber;
        return true;
    }

    // 앱이 Firebase에서 받은 등록 토큰으로 바꾼다. 바뀐 값이 있으면 true를 돌려준다
    public boolean updateFcmToken(String fcmToken) {
        if (Objects.equals(this.fcmToken, fcmToken)) {
            return false;
        }
        this.fcmToken = fcmToken;
        return true;
    }
}
