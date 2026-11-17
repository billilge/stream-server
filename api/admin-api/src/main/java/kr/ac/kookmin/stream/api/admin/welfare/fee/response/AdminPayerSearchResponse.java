package kr.ac.kookmin.stream.api.admin.welfare.fee.response;

import kr.ac.kookmin.stream.api.admin.welfare.fee.usecase.PayerMember;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.Payer;

/**
 * 납부자 명부의 한 줄. 앱에 가입하지 않은 레거시 이관 납부자는 memberId가 null이고, 학번도 null일 수 있다.
 *
 * @param isRegistered        앱에 가입한 회원인지. 관리자 등록 여부와 다르다
 * @param isCouncilRegistered 이미 관리자 관리 명단에 올라 있는지
 */
public record AdminPayerSearchResponse(
    Long payerId,
    Long memberId,
    String name,
    String studentId,
    String enrollmentYear,
    boolean isRegistered,
    String department,
    boolean isCouncilRegistered
) {
    // member는 아직 가입하지 않은 레거시 이관 납부자이거나 탈퇴한 회원이면 null이다 — 학부를 알 수 없다
    public static AdminPayerSearchResponse from(PayerMember payerMember) {
        Payer payer = payerMember.payer();
        Member member = payerMember.member();
        return new AdminPayerSearchResponse(
            payer.getId(),
            payer.getMemberId(),
            payer.getName(),
            payer.getStudentId(),
            payer.getEnrollmentYear(),
            payer.isRegistered(),
            member != null ? member.getDepartment().displayName() : null,
            member != null && member.isCouncilRegistered()
        );
    }
}
