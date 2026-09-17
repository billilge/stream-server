package kr.ac.kookmin.stream.api.admin.welfare.fee.response;

import java.time.LocalDateTime;
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
    public static AdminFeeSearchResponse of(StudentTransferStatus request, Member member) {
        return new AdminFeeSearchResponse(
            request.getId(),
            request.getMemberId(),
            member.getName(),
            member.getStudentId(),
            member.getDepartment().name(),
            request.getStatus().name(),
            request.getCreatedAt(),
            request.getReviewedAt()
        );
    }
}
