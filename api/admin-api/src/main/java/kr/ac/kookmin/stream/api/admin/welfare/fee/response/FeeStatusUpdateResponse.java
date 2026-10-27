package kr.ac.kookmin.stream.api.admin.welfare.fee.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;

public record FeeStatusUpdateResponse(
    Long studentTransferStatusId,
    Long memberId,
    String status,
    String adminNote,
    LocalDateTime reviewedAt,
    LocalDateTime confirmedAt
) {
    public static FeeStatusUpdateResponse from(StudentTransferStatus transferStatus) {
        return new FeeStatusUpdateResponse(
            transferStatus.getId(),
            transferStatus.getMemberId(),
            transferStatus.getStatus().name(),
            transferStatus.getAdminNote(),
            transferStatus.getReviewedAt(),
            // 납부 확인 시각은 납부완료일 때의 처리 시각이다
            transferStatus.getStatus() == TransferStatus.PAID ? transferStatus.getReviewedAt() : null
        );
    }
}
