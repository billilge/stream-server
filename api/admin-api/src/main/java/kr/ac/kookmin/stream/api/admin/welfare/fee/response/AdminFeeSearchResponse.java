package kr.ac.kookmin.stream.api.admin.welfare.fee.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.api.admin.welfare.fee.FeeAccount;
import kr.ac.kookmin.stream.api.common.ApiConstants;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;

/**
 * 납부자 목록의 한 줄. 모든 회원이 납부 상태 행을 가지므로 아직 아무것도 내지 않은 회원도 들어온다 —
 * 그런 줄은 상태가 납부 전이고 제출 관련 값이 모두 null이다.
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
     * @param member   탈퇴(소프트 삭제)한 회원의 행이면 null — 이름·학번·학과를 알 수 없어 표시용 placeholder를 쓴다
     * @param signedUp 회원가입(전화번호 + 필수 약관 동의)을 마쳤는지
     */
    public static AdminFeeSearchResponse of(
        StudentTransferStatus transferStatus,
        Member member,
        boolean signedUp,
        FeeAccount account
    ) {
        return new AdminFeeSearchResponse(
            transferStatus.getId(),
            transferStatus.getMemberId(),
            member != null ? member.getName() : ApiConstants.WITHDRAWN_MEMBER_LABEL,
            member != null ? member.getStudentId() : null,
            member != null ? member.getDepartment().displayName() : null,
            signedUp,
            transferStatus.getStatus().name(),
            transferStatus.getRemainingSemesters(),
            account.calculateTransferAmount(transferStatus.getRemainingSemesters()),
            StorageUrlBuilder.build(transferStatus.getProofImageKey()),
            transferStatus.getRequestedAt(),
            // 납부 확인 시각은 납부완료일 때의 처리 시각이다
            transferStatus.getStatus() == TransferStatus.PAID ? transferStatus.getReviewedAt() : null,
            transferStatus.getAdminNote()
        );
    }
}
