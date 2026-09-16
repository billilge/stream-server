package kr.ac.kookmin.stream.db.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.ac.kookmin.stream.common.CouncilDepartment;
import kr.ac.kookmin.stream.common.Role;
import kr.ac.kookmin.stream.db.common.BaseSoftDeleteEntity;
import kr.ac.kookmin.stream.member.domain.member.domain.Department;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
// uk_members_active_student_id(V1)는 generated column active_student_id에 걸려 있다.
// Entity에 매핑된 컬럼이 아니라 여기서는 선언하지 않는다.
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberJpaEntity extends BaseSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Department department;

    private String email;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "council_department", length = 30)
    private CouncilDepartment councilDepartment;

    private MemberJpaEntity(Member member) {
        this.id = member.getId();
        this.studentId = member.getStudentId();
        this.name = member.getName();
        this.department = member.getDepartment();
        this.email = member.getEmail();
        this.fcmToken = member.getFcmToken();
        this.role = member.getRole();
        this.councilDepartment = member.getCouncilDepartment();
    }

    public static MemberJpaEntity from(Member member) {
        return new MemberJpaEntity(member);
    }

    public Member toDomain() {
        return Member.of(id, studentId, name, department, email, fcmToken, role, councilDepartment);
    }
}
