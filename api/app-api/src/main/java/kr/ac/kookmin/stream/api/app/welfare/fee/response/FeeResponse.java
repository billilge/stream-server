package kr.ac.kookmin.stream.api.app.welfare.fee.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;

public record FeeResponse(
    String feeStatus,
    String transferLinkUrl,
    LocalDateTime requestedAt,
    LocalDateTime confirmedAt
) {
    public static FeeResponse of(StudentTransferStatus request, String transferLinkUrl) {
        LocalDateTime confirmedAt = request.getStatus() == TransferStatus.PAID ? request.getReviewedAt() : null;
        // student_transfer_statuses엔 별도 requested_at 컬럼이 없어, 확인 요청 시각은 row의 created_at으로 갈음한다.
        return new FeeResponse(request.getStatus().name(), transferLinkUrl, request.getCreatedAt(), confirmedAt);
    }
}
