package kr.ac.kookmin.stream.api.admin.welfare.fee.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.ApiConstants;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;

public record AdminFeeSearchResponse(
    Long studentTransferStatusId,
    Long memberId,
    String name,
    String studentId,
    String department,
    String status,
    LocalDateTime requestedAt,
    LocalDateTime reviewedAt
) {
    // member가 null이면 탈퇴(소프트 삭제)한 회원의 과거 요청이다 — 이름/학번/학과를 알 수 없으니 표시용 placeholder를 쓴다.
    public static AdminFeeSearchResponse of(StudentTransferStatus status, Member member) {
        return new AdminFeeSearchResponse(
            status.getId(),
            status.getMemberId(),
            member != null ? member.getName() : ApiConstants.WITHDRAWN_MEMBER_LABEL,
            member != null ? member.getStudentId() : null,
            member != null ? member.getDepartment().name() : null,
            status.getStatus().name(),
            status.getCreatedAt(),
            status.getReviewedAt()
        );
    }
}
