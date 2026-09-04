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

    @Column(name = "is_fee_paid", nullable = false)
    private boolean feePaid;

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
        this.feePaid = member.isFeePaid();
        this.role = member.getRole();
        this.councilDepartment = member.getCouncilDepartment();
    }

    public static MemberJpaEntity from(Member member) {
        return new MemberJpaEntity(member);
    }

    public Member toDomain() {
        return Member.of(id, studentId, name, department, email, fcmToken, feePaid, role, councilDepartment);
    }
}
