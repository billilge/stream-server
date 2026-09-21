package kr.ac.kookmin.stream.api.admin.welfare.fee.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;

public record FeeStatusUpdateResponse(
    Long studentTransferStatusId,
    Long memberId,
    String status,
    LocalDateTime reviewedAt
) {
    public static FeeStatusUpdateResponse from(StudentTransferStatus request) {
        return new FeeStatusUpdateResponse(request.getId(), request.getMemberId(), request.getStatus().name(), request.getReviewedAt());
    }
}
