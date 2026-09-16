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
        return new FeeResponse(fee.getStatus().name(), paymentLinkUrl, fee.getRequestedAt(), confirmedAt);
    }
}
