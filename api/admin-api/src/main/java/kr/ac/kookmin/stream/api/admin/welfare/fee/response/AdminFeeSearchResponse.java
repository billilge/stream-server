package kr.ac.kookmin.stream.api.admin.welfare.fee.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.api.admin.welfare.fee.FeeAccount;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;

/**
 * 납부자 목록의 한 줄. 목록은 회원 기준이라 납부 정보를 내지 않은 회원도 들어온다 —
 * 그런 회원은 {@code studentTransferStatusId}를 비롯한 납부 관련 값이 모두 null이고 상태는 납부 전이다.
 */
public record AdminFeeSearchResponse(
    Long studentTransferStatusId,
    Long memberId,
    String name,
    String studentId,
    String department,
    boolean isStreamMember,
    String feeStatus,
    Integer remainingSemesters,
    Long transferAmount,
    String proofImageUrl,
    LocalDateTime requestedAt,
    LocalDateTime confirmedAt,
    String adminNote
) {
    /**
     * @param transferStatus 납부 정보를 내지 않은 회원이면 null
     * @param signedUp       회원가입(전화번호 + 필수 약관 동의)을 마쳤는지
     */
    public static AdminFeeSearchResponse of(
        Member member,
        StudentTransferStatus transferStatus,
        boolean signedUp,
        FeeAccount account
    ) {
        if (transferStatus == null) {
            return new AdminFeeSearchResponse(
                null, member.getId(), member.getName(), member.getStudentId(),
                member.getDepartment().displayName(), signedUp, TransferStatus.UNPAID.name(),
                null, null, null, null, null, null);
        }

        boolean paid = transferStatus.getStatus() == TransferStatus.PAID;

        return new AdminFeeSearchResponse(
            transferStatus.getId(),
            member.getId(),
            member.getName(),
            member.getStudentId(),
            member.getDepartment().displayName(),
            signedUp,
            transferStatus.getStatus().name(),
            transferStatus.getRemainingSemesters(),
            account.transferAmountFor(transferStatus.getRemainingSemesters()),
            StorageUrlBuilder.build(transferStatus.getProofImageKey()),
            transferStatus.getRequestedAt(),
            paid ? transferStatus.getReviewedAt() : null,
            transferStatus.getAdminNote()
        );
    }
}
