package kr.ac.kookmin.stream.api.admin.welfare.fee.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.api.common.ApiConstants;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;

/**
 * 납부자 목록의 한 줄. 납부를 요청한 회원만 들어온다 — 한 번도 요청하지 않은 회원은 행이 없다.
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
    String rejectReason
) {
    /**
     * @param member   탈퇴(소프트 삭제)한 회원의 행이면 null — 이름·학번·학과를 알 수 없어 표시용 placeholder를 쓴다
     * @param signedUp 회원가입(전화번호 + 필수 약관 동의)을 마쳤는지
     */
    public static AdminFeeSearchResponse of(
        StudentTransferStatus transferStatus,
        Member member,
        boolean signedUp
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
            // 요청 시점에 남긴 금액이다. 운영진이 학기당 회비를 바꿔도 이미 들어온 요청의 금액은 그대로 보인다
            transferStatus.getAmount(),
            StorageUrlBuilder.build(transferStatus.getProofImageKey()),
            transferStatus.getRequestedAt(),
            transferStatus.getConfirmedAt(),
            transferStatus.getRejectReason()
        );
    }
}
