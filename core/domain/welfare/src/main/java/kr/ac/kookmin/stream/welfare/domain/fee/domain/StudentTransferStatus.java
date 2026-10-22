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

    private Long id;
    private Long memberId;
    private TransferStatus status;
    // 학생이 입력한 남은 학기 수와, 그 근거로 올린 학점이수현황 캡처. 제출 전에는 둘 다 null이다
    private Integer remainingSemesters;
    private String proofImageKey;
    // 관리자가 확인필요로 처리하며 남긴 안내 문구. NEEDS_CHECK일 때만 값이 있다
    private String adminNote;
    private LocalDateTime requestedAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 납부 정보를 처음 제출하는 회원. 상태는 납부 전에서 시작하고, 값은 submit이 채운다
    public static StudentTransferStatus create(Long memberId) {
        return new StudentTransferStatus(
            null, memberId, TransferStatus.UNPAID, null, null, null, null, null, null, null);
    }

    // 아직 아무것도 제출하지 않아 DB에 row가 없는 회원을 위한 가상의 기본값 (저장하지 않는다).
    // create와 상태가 같아졌으므로 둘의 차이는 "저장되는가" 하나뿐이다.
    public static StudentTransferStatus notRequested(Long memberId) {
        return new StudentTransferStatus(
            null, memberId, TransferStatus.UNPAID, null, null, null, null, null, null, null);
    }

    public static StudentTransferStatus of(
        Long id,
        Long memberId,
        TransferStatus status,
        Integer remainingSemesters,
        String proofImageKey,
        String adminNote,
        LocalDateTime requestedAt,
        LocalDateTime reviewedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        return new StudentTransferStatus(id, memberId, status, remainingSemesters, proofImageKey,
            adminNote, requestedAt, reviewedAt, createdAt, updatedAt);
    }

    /**
     * 납부 정보(남은 학기 수·학점이수현황 캡처)를 제출한다. 상태는 납부 전 그대로다 —
     * 송금 확인 요청({@link #requestConfirmation()})을 해야 납부확인중으로 넘어간다.
     * <p>
     * 납부 전에서만 가능하다. 확인필요로 처리된 회원은 직접 다시 제출하지 못하고,
     * 관리자가 납부 전으로 되돌려야 한다(화면의 진입점이 "문의하기" 하나뿐이다).
     */
    public void submit(int remainingSemesters, String proofImageKey) {
        requireUnpaid();

        this.remainingSemesters = remainingSemesters;
        this.proofImageKey = proofImageKey;
    }

    /**
     * 송금 확인을 요청한다. 이전 처리 흔적(관리자 안내 문구·처리 시각)은 지워 새 요청으로 만든다.
     */
    public void requestConfirmation(LocalDateTime now) {
        requireUnpaid();

        this.status = TransferStatus.PENDING;
        this.requestedAt = now;
        this.adminNote = null;
        this.reviewedAt = null;
    }

    /**
     * 관리자가 납부 상태를 바꾼다. 이미 처리한 요청도 다시 바꿀 수 있다 — 잘못 처리한 경우를 되돌리기 위해서다.
     * 그래서 납부완료도 종결 상태가 아니다.
     * <p>
     * 안내 문구는 확인필요일 때만 남긴다. 납부확인중으로 되돌리면 아직 처리 전이라는 뜻이므로 처리 시각도 지운다.
     */
    public void review(TransferStatus reviewedStatus, String adminNote, LocalDateTime now) {
        this.status = reviewedStatus;
        this.adminNote = reviewedStatus == TransferStatus.NEEDS_CHECK ? adminNote : null;
        this.reviewedAt = reviewedStatus == TransferStatus.PENDING ? null : now;
    }

    private void requireUnpaid() {
        if (status != TransferStatus.UNPAID) {
            throw new BusinessException(FeeErrorCode.FEE_REQUEST_ALREADY_REQUESTED);
        }
    }
}
