package kr.ac.kookmin.stream.db.welfare;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import kr.ac.kookmin.stream.db.common.BaseTimeEntity;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.PaymentStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentFee;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "student_fees")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudentFeeJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_fee_id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus;

    @Column(name = "payment_link_url", length = 500)
    private String paymentLinkUrl;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "confirmed_by")
    private Long confirmedBy;

    private StudentFeeJpaEntity(StudentFee fee) {
        this.id = fee.getId();
        this.memberId = fee.getMemberId();
        this.amount = fee.getAmount();
        this.paymentStatus = fee.getPaymentStatus();
        this.paymentLinkUrl = fee.getPaymentLinkUrl();
        this.paidAt = fee.getPaidAt();
        this.confirmedBy = fee.getConfirmedBy();
    }

    public static StudentFeeJpaEntity from(StudentFee fee) {
        return new StudentFeeJpaEntity(fee);
    }

    public StudentFee toDomain() {
        return StudentFee.of(id, memberId, amount, paymentStatus, paymentLinkUrl, paidAt, confirmedBy);
    }
}
