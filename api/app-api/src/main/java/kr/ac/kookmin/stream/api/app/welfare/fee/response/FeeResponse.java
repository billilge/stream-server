package kr.ac.kookmin.stream.api.app.welfare.fee.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.PaymentStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentFee;

public record FeeResponse(
    String feeStatus,
    String paymentLinkUrl,
    LocalDateTime requestedAt,
    LocalDateTime confirmedAt
) {
    public static FeeResponse of(StudentFee fee, String paymentLinkUrl) {
        LocalDateTime confirmedAt = fee.getStatus() == PaymentStatus.PAID ? fee.getReviewedAt() : null;
        // student_fees엔 별도 requested_at 컬럼이 없어, 확인 요청 시각은 row의 created_at으로 갈음한다.
        return new FeeResponse(fee.getStatus().name(), paymentLinkUrl, fee.getCreatedAt(), confirmedAt);
    }
}
