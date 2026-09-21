package kr.ac.kookmin.stream.db.welfare;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import kr.ac.kookmin.stream.db.common.BaseTimeEntity;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.Payer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "payers",
    uniqueConstraints = @UniqueConstraint(name = "uk_payers_member_id", columnNames = "member_id")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PayerJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payer_id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String name;

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "enrollment_year", nullable = false)
    private String enrollmentYear;

    @Column(nullable = false)
    private boolean registered;

    private PayerJpaEntity(Payer payer) {
        this.id = payer.getId();
        this.memberId = payer.getMemberId();
        this.name = payer.getName();
        this.studentId = payer.getStudentId();
        this.enrollmentYear = payer.getEnrollmentYear();
        this.registered = payer.isRegistered();
    }

    public static PayerJpaEntity from(Payer payer) {
        return new PayerJpaEntity(payer);
    }

    public Payer toDomain() {
        return Payer.of(id, memberId, name, studentId, enrollmentYear, registered);
    }
}
