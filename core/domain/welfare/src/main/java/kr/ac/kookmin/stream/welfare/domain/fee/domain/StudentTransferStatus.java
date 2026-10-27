package kr.ac.kookmin.stream.welfare.domain.fee.domain;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.common.BusinessException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StudentTransferStatus {

    // admin_note 컬럼 길이
    private static final int MAX_REJECT_REASON_LENGTH = 255;

    private Long id;
    private Long memberId;
    private TransferStatus status;
    // 학생이 입력한 남은 학기 수와, 그 근거로 올린 학점이수현황 캡처
    private Integer remainingSemesters;
    private String proofImageKey;
    // 요청 시점에 안내한 납부 금액(남은 학기 수 × 그때의 학기당 회비). 운영진이 학기당 회비를 바꿔도 그대로다
    private Long amount;
    // 관리자가 확인필요로 처리하며 학생에게 남긴 반려 사유. REJECTED일 때만 값이 있다
    private String rejectReason;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 송금을 마친 학생의 납부 요청. 학기 인증(남은 학기 수·학점이수현황 캡처)과 송금 완료를 한 번에 받으므로
     * 바로 납부확인중이 된다. 처음 내든 반려 뒤 다시 내든 늘 새 요청(행)이고, 이전 요청은 이력으로 남는다.
     */
    public static StudentTransferStatus create(
        Long memberId,
        int remainingSemesters,
        String proofImageKey,
        long amount
    ) {
        return new StudentTransferStatus(null, memberId, TransferStatus.PENDING, remainingSemesters, proofImageKey,
            amount, null, null, null, null);
    }

    public static StudentTransferStatus of(
        Long id,
        Long memberId,
        TransferStatus status,
        Integer remainingSemesters,
        String proofImageKey,
        Long amount,
        String rejectReason,
        LocalDateTime reviewedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        return new StudentTransferStatus(id, memberId, status, remainingSemesters, proofImageKey,
            amount, rejectReason, reviewedAt, createdAt, updatedAt);
    }

    /**
     * 이 요청 다음에 학생이 새 납부 요청을 낼 수 있는지. 반려된 요청 뒤에만 다시 낼 수 있다 —
     * 납부확인중이면 이미 운영진에게 요청이 가 있고, 납부완료면 더 낼 것이 없다.
     */
    public boolean isRequestable() {
        return status == TransferStatus.REJECTED;
    }

    public void validateRequestable() {
        if (!isRequestable()) {
            throw new BusinessException(FeeErrorCode.FEE_REQUEST_ALREADY_REQUESTED);
        }
    }

    // 요청마다 새 행을 만들므로 행이 생긴 시각이 곧 요청 시각이다
    public LocalDateTime getRequestedAt() {
        return createdAt;
    }

    /** 납부 확인 시각. 납부완료일 때의 처리 시각이다. */
    public LocalDateTime getConfirmedAt() {
        return status == TransferStatus.PAID ? reviewedAt : null;
    }

    /**
     * 관리자가 납부 상태를 바꾼다. 이미 처리한 요청도 다시 바꿀 수 있다 — 잘못 처리한 경우를 되돌리기 위해서다.
     * 그래서 납부완료도 종결 상태가 아니다.
     * <p>
     * 반려 사유는 확인필요일 때만 남기고, 확인필요면 반드시 있어야 한다 — 학생이 사유를 보고 다시 내기 때문이다.
     * 납부확인중으로 되돌리면 아직 처리 전이라는 뜻이므로 처리 시각도 지운다.
     */
    public void review(TransferStatus reviewedStatus, String rejectReason, LocalDateTime now) {
        validateRejectReason(reviewedStatus, rejectReason);

        this.status = reviewedStatus;
        this.rejectReason = reviewedStatus == TransferStatus.REJECTED ? rejectReason.strip() : null;
        this.reviewedAt = reviewedStatus == TransferStatus.PENDING ? null : now;
    }

    // 확인필요는 학생에게 보여줄 반려 사유가 있어야 의미가 있다. 그 외 상태에서는 사유를 받아도 버린다
    private static void validateRejectReason(TransferStatus status, String rejectReason) {
        if (status != TransferStatus.REJECTED) {
            return;
        }
        if (rejectReason == null || rejectReason.isBlank() || rejectReason.strip().length() > MAX_REJECT_REASON_LENGTH) {
            throw new BusinessException(FeeErrorCode.INVALID_REJECT_REASON);
        }
    }
}
