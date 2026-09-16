package kr.ac.kookmin.stream.welfare.domain.fee.domain;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StudentFee {

    private Long id;
    private Long memberId;
    private PaymentStatus status;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static StudentFee create(Long memberId) {
        return new StudentFee(null, memberId, PaymentStatus.PENDING, null, null, null);
    }

    // 아직 확인 요청을 한 번도 보내지 않은 회원을 위한 가상의 기본값 (DB에 row가 없음, 저장하지 않는다)
    public static StudentFee notRequested(Long memberId) {
        return new StudentFee(null, memberId, PaymentStatus.UNPAID, null, null, null);
    }

    public static StudentFee of(
        Long id,
        Long memberId,
        PaymentStatus status,
        LocalDateTime reviewedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        return new StudentFee(id, memberId, status, reviewedAt, createdAt, updatedAt);
    }

    // 확인 요청(POST /v1/app/fee) 시각은 별도 컬럼 없이 row의 created_at(최초 생성) 또는
    // 재요청 시점의 updated_at으로 갈음한다 — student_fees 테이블에 requested_at 컬럼을 두지 않기로 했다(ERD 기준).
    public StudentFee requestConfirmation() {
        return new StudentFee(id, memberId, PaymentStatus.PENDING, null, createdAt, updatedAt);
    }

    public StudentFee review(PaymentStatus reviewedStatus, LocalDateTime now) {
        return new StudentFee(id, memberId, reviewedStatus, now, createdAt, updatedAt);
    }
}
