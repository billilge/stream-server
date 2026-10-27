package kr.ac.kookmin.stream.api.app.welfare.fee.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeAccount;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;

/**
 * 학생회비 납부 현황. 화면은 feeStatus로 분기한다 — 납부 전(UNPAID)·확인필요(REJECTED)면 남은 학기 입력부터,
 * 납부확인중·납부완료면 요청 내용 확인 화면이다.
 * <p>
 * 금액과 송금 링크는 지금 낼 수 있을 때만 계산해 준다. 납부 전·확인필요인 학생이 남은 학기 수를 보내면 그 값으로
 * 계산하고, 납부확인중·납부완료면 요청할 때 남긴 금액만 보여주고 링크는 주지 않는다("또 보내라"로 읽히지 않게).
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

    // 한 번도 요청하지 않은 회원의 상태. 도메인 상태에는 없고(행이 없다) 응답에서만 쓰는 이름이다
    private static final String NOT_REQUESTED_STATUS = "UNPAID";

    /** 한 번도 요청하지 않은 학생. 남은 학기 수를 받았으면 그 값으로 금액과 송금 링크를 계산한다. */
    public static FeeResponse notRequested(FeeAccount account, Integer remainingSemesters) {
        Long transferAmount = calculateTransferAmount(account, remainingSemesters);

        return new FeeResponse(null, NOT_REQUESTED_STATUS, account.bank(), account.accountNo(),
            remainingSemesters, transferAmount, generateTransferLinkUrl(account, transferAmount), null, null, null);
    }

    /** 요청이 있는 학생. remainingSemesters는 확인필요(다시 낼 수 있는 상태)일 때만 쓴다. */
    public static FeeResponse of(StudentTransferStatus transferStatus, FeeAccount account, Integer remainingSemesters) {
        TransferStatus status = transferStatus.getStatus();

        // 반려면 다시 낼 수 있으므로 미납부와 같이 다룬다 — 이전 요청 값 대신 이번에 받은 남은 학기 수로 계산한다
        if (status == TransferStatus.REJECTED) {
            Long transferAmount = calculateTransferAmount(account, remainingSemesters);
            return new FeeResponse(transferStatus.getId(), status.name(), account.bank(), account.accountNo(),
                remainingSemesters, transferAmount, generateTransferLinkUrl(account, transferAmount),
                transferStatus.getRequestedAt(), null, transferStatus.getAdminNote());
        }

        return new FeeResponse(transferStatus.getId(), status.name(), account.bank(), account.accountNo(),
            transferStatus.getRemainingSemesters(), transferStatus.getAmount(), null,
            transferStatus.getRequestedAt(),
            // 납부 확인 시각은 PAID일 때의 처리 시각이다
            status == TransferStatus.PAID ? transferStatus.getReviewedAt() : null,
            null);
    }

    // 남은 학기 수를 받지 않았으면 금액도 송금 링크도 계산하지 않는다
    private static Long calculateTransferAmount(FeeAccount account, Integer remainingSemesters) {
        return remainingSemesters == null ? null : account.calculateTransferAmount(remainingSemesters);
    }

    private static String generateTransferLinkUrl(FeeAccount account, Long transferAmount) {
        return transferAmount == null ? null : account.generateTransferLinkUrl(transferAmount);
    }
}
