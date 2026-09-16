package kr.ac.kookmin.stream.api.admin.welfare.fee.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentFee;

public record AdminFeeSearchResponse(
    Long studentFeeId,
    Long memberId,
    String name,
    String studentId,
    String department,
    String status,
    LocalDateTime requestedAt,
    LocalDateTime reviewedAt
) {
    public static AdminFeeSearchResponse of(StudentFee fee, Member member) {
        return new AdminFeeSearchResponse(
            fee.getId(),
            fee.getMemberId(),
            member.getName(),
            member.getStudentId(),
            member.getDepartment().name(),
            fee.getStatus().name(),
            fee.getCreatedAt(),
            fee.getReviewedAt()
        );
    }
}
