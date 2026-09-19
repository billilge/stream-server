package kr.ac.kookmin.stream.welfare.domain.fee.domain;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StudentTransferStatus {

    private Long id;
    private Long memberId;
    private TransferStatus status;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static StudentTransferStatus create(Long memberId) {
        return new StudentTransferStatus(null, memberId, TransferStatus.PENDING, null, null, null);
    }

    // 아직 확인 요청을 한 번도 보내지 않은 회원을 위한 가상의 기본값 (DB에 row가 없음, 저장하지 않는다)
    public static StudentTransferStatus notRequested(Long memberId) {
        return new StudentTransferStatus(null, memberId, TransferStatus.UNPAID, null, null, null);
    }

    public static StudentTransferStatus of(
        Long id,
        Long memberId,
        TransferStatus status,
        LocalDateTime reviewedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        return new StudentTransferStatus(id, memberId, status, reviewedAt, createdAt, updatedAt);
    }

    // 확인 요청(POST /v1/app/fee) 시각은 별도 컬럼 없이 row의 created_at(최초 생성) 또는
    // 재요청 시점의 updated_at으로 갈음한다 — student_transfer_statuses 테이블에 requested_at 컬럼을 두지 않기로 했다(ERD 기준).
    // PAID는 최종 상태다 — 이미 승인된 회원이 다시 요청해도 상태를 되돌리지 않고 그대로 둔다.
    public void requestConfirmation() {
        if (status == TransferStatus.PAID) {
            return;
        }

        this.status = TransferStatus.PENDING;
        this.reviewedAt = null;
    }

    public void review(TransferStatus reviewedStatus, LocalDateTime now) {
        this.status = reviewedStatus;
        this.reviewedAt = now;
    }
}
