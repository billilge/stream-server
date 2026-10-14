package kr.ac.kookmin.stream.api.app.welfare.fee.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.api.app.welfare.fee.FeeAccount;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;

/**
 * 학생회비 납부 현황. 화면은 feeStatus와 studentTransferStatusId로 분기한다 —
 * 납부 전이면서 id가 null이면 남은 학기 입력, id가 있으면 송금 완료 여부 확인 화면이다.
 */
public record FeeResponse(
    Long studentTransferStatusId,
    String feeStatus,
    String bank,
    String accountNo,
    Integer remainingSemesters,
    Long transferAmount,
    String transferLinkUrl,
    LocalDateTime requestedAt,
    LocalDateTime confirmedAt,
    String adminNote
) {
    public static FeeResponse of(StudentTransferStatus transferStatus, FeeAccount account) {
        boolean paid = transferStatus.getStatus() == TransferStatus.PAID;
        boolean needsCheck = transferStatus.getStatus() == TransferStatus.NEEDS_CHECK;

        return new FeeResponse(
            transferStatus.getId(),
            transferStatus.getStatus().name(),
            account.bank(),
            account.accountNo(),
            transferStatus.getRemainingSemesters(),
            account.transferAmount(),
            account.transferLinkUrl(),
            transferStatus.getRequestedAt(),
            // 납부 확인 시각은 PAID일 때의 처리 시각이다. 그 외 상태에서는 보여주지 않는다
            paid ? transferStatus.getReviewedAt() : null,
            needsCheck ? transferStatus.getAdminNote() : null
        );
    }
}
