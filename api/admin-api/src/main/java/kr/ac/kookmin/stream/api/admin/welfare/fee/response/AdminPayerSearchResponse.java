package kr.ac.kookmin.stream.api.admin.welfare.fee.response;

import kr.ac.kookmin.stream.welfare.domain.fee.domain.Payer;

/**
 * 납부자 명부의 한 줄. 앱에 가입하지 않은 레거시 이관 납부자는 memberId가 null이고, 학번도 null일 수 있다.
 */
public record AdminPayerSearchResponse(
    Long payerId,
    Long memberId,
    String name,
    String studentId,
    String enrollmentYear,
    boolean isRegistered
) {
    public static AdminPayerSearchResponse from(Payer payer) {
        return new AdminPayerSearchResponse(
            payer.getId(),
            payer.getMemberId(),
            payer.getName(),
            payer.getStudentId(),
            payer.getEnrollmentYear(),
            payer.isRegistered()
        );
    }
}
