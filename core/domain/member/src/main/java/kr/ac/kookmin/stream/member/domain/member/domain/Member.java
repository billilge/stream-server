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

    // 가입 시점의 기본 납부 상태. welfare의 TransferStatus.UNPAID와 같은 문자열이다
    private static final String UNPAID = "UNPAID";

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
    /**
     * 학생회비 납부 상태(UNPAID / PENDING / PAID / NEEDS_CHECK).
     * <p>
     * 실제 상태는 welfare 모듈의 {@code student_transfer_statuses}가 들고 있고, 여기 있는 값은 그 사본이다.
     * 관리자 납부자 목록이 "납부 전" 회원까지 보여줘야 하는데 그런 회원은 welfare 쪽에 행이 없어,
     * 회원 기준으로 검색·필터·페이징하려면 상태가 회원 쪽에도 있어야 한다.
     * <p>
     * member 모듈은 이 값을 해석하지 않고 저장·비교만 하므로 welfare의 {@code TransferStatus}를 끌어오지 않고
     * 문자열로 둔다(도메인 모듈끼리는 서로를 모른다). 두 값은 항상 같은 트랜잭션에서 함께 바꾼다.
     */
    private String feeStatus;

    // 로그인으로 처음 가입하는 회원. 운영진 권한·학생회 부서는 가입 뒤 따로 부여한다
    public static Member create(String studentId, String name, Department department, String academicStatus) {
        return new Member(
            null, studentId, name, department, academicStatus, null, null, null, Role.STUDENT, null, UNPAID);
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
        CouncilDepartment councilDepartment,
        String feeStatus
    ) {
        return new Member(id, studentId, name, department, academicStatus, email, phoneNumber, fcmToken,
            role, councilDepartment, feeStatus);
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

    // 회비 납부 상태를 welfare 쪽 변경에 맞춰 반영한다. 호출부가 두 값을 한 트랜잭션에서 함께 바꾼다
    public void changeFeeStatus(String feeStatus) {
        this.feeStatus = feeStatus;
    }

    public void registerPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean hasPhoneNumber() {
        return phoneNumber != null;
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
