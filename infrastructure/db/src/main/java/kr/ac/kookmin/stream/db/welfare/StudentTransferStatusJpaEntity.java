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
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 현재는 학생회장/총무부장 등 소수 인원만 처리 권한을 가져 낙관적 락(@Version)을 쓰지 않는다.
 * 처리 권한자가 여러 명으로 늘어나면 동시 처리 경합을 막기 위해 @Version 도입을 검토한다.
 */
@Entity
@Table(
    name = "student_transfer_statuses",
    uniqueConstraints = @UniqueConstraint(name = "uk_student_transfer_statuses_member_id", columnNames = "member_id")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudentTransferStatusJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_transfer_status_id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransferStatus status;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    private StudentTransferStatusJpaEntity(StudentTransferStatus request) {
        this.id = request.getId();
        this.memberId = request.getMemberId();
        this.status = request.getStatus();
        this.reviewedAt = request.getReviewedAt();
    }

    public static StudentTransferStatusJpaEntity from(StudentTransferStatus request) {
        return new StudentTransferStatusJpaEntity(request);
    }

    public StudentTransferStatus toDomain() {
        return StudentTransferStatus.of(id, memberId, status, reviewedAt, getCreatedAt(), getUpdatedAt());
    }
}
