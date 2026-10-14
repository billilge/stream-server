package kr.ac.kookmin.stream.api.app.welfare.fee.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.api.app.welfare.fee.FeeTransferInfo;
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
    /**
     * 확인필요에서는 금액과 송금 링크를 내려주지 않는다. 아직 납부가 확인되지 않아 화면이 납부금액을
     * "-"로 두고, 송금 링크를 다시 주면 "또 보내라"로 읽히기 때문이다.
     * 재제출은 관리자가 납부 전으로 되돌린 뒤에 열린다.
     */
    public static FeeResponse of(StudentTransferStatus transferStatus, FeeTransferInfo transferInfo) {
        boolean paid = transferStatus.getStatus() == TransferStatus.PAID;
        boolean needsCheck = transferStatus.getStatus() == TransferStatus.NEEDS_CHECK;

        return new FeeResponse(
            transferStatus.getId(),
            transferStatus.getStatus().name(),
            transferInfo.bank(),
            transferInfo.accountNo(),
            transferStatus.getRemainingSemesters(),
            needsCheck ? null : transferInfo.transferAmount(),
            needsCheck ? null : transferInfo.transferLinkUrl(),
            transferStatus.getRequestedAt(),
            // 납부 확인 시각은 PAID일 때의 처리 시각이다. 그 외 상태에서는 보여주지 않는다
            paid ? transferStatus.getReviewedAt() : null,
            needsCheck ? transferStatus.getAdminNote() : null
        );
    }
}
