package kr.ac.kookmin.stream.db.welfare;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import kr.ac.kookmin.stream.db.common.BaseTimeEntity;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.PaymentStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentFee;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 현재는 학생회장/총무부장 등 소수 인원만 처리 권한을 가져 낙관적 락(@Version)을 쓰지 않는다.
 * 처리 권한자가 여러 명으로 늘어나면 동시 처리 경합을 막기 위해 @Version 도입을 검토한다.
 */
@Entity
@Table(
    name = "student_fees",
    uniqueConstraints = @UniqueConstraint(name = "uk_student_fees_member_id", columnNames = "member_id")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudentFeeJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_fee_id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    private StudentFeeJpaEntity(StudentFee fee) {
        this.id = fee.getId();
        this.memberId = fee.getMemberId();
        this.status = fee.getStatus();
        this.reviewedAt = fee.getReviewedAt();
    }

    public static StudentFeeJpaEntity from(StudentFee fee) {
        return new StudentFeeJpaEntity(fee);
    }

    public StudentFee toDomain() {
        return StudentFee.of(id, memberId, status, reviewedAt, getCreatedAt(), getUpdatedAt());
    }
}
