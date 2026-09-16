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
    private LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static StudentFee create(Long memberId, LocalDateTime now) {
        return new StudentFee(null, memberId, PaymentStatus.PENDING, now, null, null, null);
    }

    // 아직 확인 요청을 한 번도 보내지 않은 회원을 위한 가상의 기본값 (DB에 row가 없음, 저장하지 않는다)
    public static StudentFee notRequested(Long memberId) {
        return new StudentFee(null, memberId, PaymentStatus.UNPAID, null, null, null, null);
    }

    public static StudentFee of(
        Long id,
        Long memberId,
        PaymentStatus status,
        LocalDateTime requestedAt,
        LocalDateTime reviewedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        return new StudentFee(id, memberId, status, requestedAt, reviewedAt, createdAt, updatedAt);
    }

    public StudentFee requestConfirmation(LocalDateTime now) {
        return new StudentFee(id, memberId, PaymentStatus.PENDING, now, null, createdAt, updatedAt);
    }

    public StudentFee review(PaymentStatus reviewedStatus, LocalDateTime now) {
        return new StudentFee(id, memberId, reviewedStatus, requestedAt, now, createdAt, updatedAt);
    }
}
