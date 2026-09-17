package kr.ac.kookmin.stream.api.app.welfare.fee.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentFee;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;

public record FeeResponse(
    String feeStatus,
    String transferLinkUrl,
    LocalDateTime requestedAt,
    LocalDateTime confirmedAt
) {
    public static FeeResponse of(StudentFee fee, String transferLinkUrl) {
        LocalDateTime confirmedAt = fee.getStatus() == TransferStatus.PAID ? fee.getReviewedAt() : null;
        // student_fees엔 별도 requested_at 컬럼이 없어, 확인 요청 시각은 row의 created_at으로 갈음한다.
        return new FeeResponse(fee.getStatus().name(), transferLinkUrl, fee.getCreatedAt(), confirmedAt);
    }
}
