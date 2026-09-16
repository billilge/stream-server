package kr.ac.kookmin.stream.api.admin.welfare.fee.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentFee;

public record FeeStatusUpdateResponse(
    Long studentFeeId,
    Long memberId,
    String status,
    LocalDateTime reviewedAt
) {
    public static FeeStatusUpdateResponse from(StudentFee fee) {
        return new FeeStatusUpdateResponse(fee.getId(), fee.getMemberId(), fee.getStatus().name(), fee.getReviewedAt());
    }
}
