package kr.ac.kookmin.stream.api.app.welfare.fee.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;

/** 송금 확인 요청 결과. */
public record FeeConfirmResponse(
    Long studentTransferStatusId,
    String feeStatus,
    LocalDateTime requestedAt
) {
    public static FeeConfirmResponse from(StudentTransferStatus transferStatus) {
        return new FeeConfirmResponse(
            transferStatus.getId(), transferStatus.getStatus().name(), transferStatus.getRequestedAt());
    }
}
