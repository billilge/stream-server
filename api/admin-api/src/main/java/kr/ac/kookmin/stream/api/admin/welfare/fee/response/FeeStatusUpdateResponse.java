package kr.ac.kookmin.stream.api.admin.welfare.fee.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferRequest;

public record FeeStatusUpdateResponse(
    Long studentTransferRequestId,
    Long memberId,
    String status,
    LocalDateTime reviewedAt
) {
    public static FeeStatusUpdateResponse from(StudentTransferRequest request) {
        return new FeeStatusUpdateResponse(request.getId(), request.getMemberId(), request.getStatus().name(), request.getReviewedAt());
    }
}
