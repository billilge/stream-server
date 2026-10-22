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
    /**
     * 금액과 송금 링크는 보낼 금액을 정할 수 있을 때만 내려준다. 제출 전이면 남은 학기 수를 모르고,
     * 확인필요면 아직 납부가 확인되지 않아 화면이 납부금액을 "-"로 둔다. 확인필요에서 송금 링크를
     * 다시 주면 "또 보내라"로 읽히는 것도 이유다. 재제출은 관리자가 납부 전으로 되돌린 뒤에 열린다.
     */
    public static FeeResponse of(StudentTransferStatus transferStatus, FeeAccount account) {
        Integer remainingSemesters = transferStatus.getRemainingSemesters();
        boolean payable = remainingSemesters != null && transferStatus.getStatus() != TransferStatus.NEEDS_CHECK;

        Long transferAmount = payable ? account.transferAmountFor(remainingSemesters) : null;

        return new FeeResponse(
            transferStatus.getId(),
            transferStatus.getStatus().name(),
            account.bank(),
            account.accountNo(),
            remainingSemesters,
            transferAmount,
            transferAmount == null ? null : account.transferLinkFor(transferAmount),
            transferStatus.getRequestedAt(),
            // 납부 확인 시각은 PAID일 때의 처리 시각이다. 그 외 상태에서는 보여주지 않는다
            transferStatus.getStatus() == TransferStatus.PAID ? transferStatus.getReviewedAt() : null,
            transferStatus.getStatus() == TransferStatus.NEEDS_CHECK ? transferStatus.getAdminNote() : null
        );
    }
}
