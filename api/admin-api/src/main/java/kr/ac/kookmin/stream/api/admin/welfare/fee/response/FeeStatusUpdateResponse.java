package kr.ac.kookmin.stream.api.admin.welfare.fee.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;

public record FeeStatusUpdateResponse(
    Long studentTransferStatusId,
    Long memberId,
    String status,
    String rejectReason,
    LocalDateTime reviewedAt,
    LocalDateTime confirmedAt
) {
    public static FeeStatusUpdateResponse from(StudentTransferStatus transferStatus) {
        return new FeeStatusUpdateResponse(
            transferStatus.getId(),
            transferStatus.getMemberId(),
            transferStatus.getStatus().name(),
            transferStatus.getRejectReason(),
            transferStatus.getReviewedAt(),
            transferStatus.getConfirmedAt()
        );
    }
}
