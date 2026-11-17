package kr.ac.kookmin.stream.api.admin.welfare.fee.usecase;

import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.Payer;

// AdminPayerSearchUseCase가 조합한 결과. DTO 변환은 Controller가 한다(MemberFeeStatus와 같은 이유).
// member는 아직 가입하지 않은 레거시 이관 납부자이거나 탈퇴한 회원이면 null이다.
public record PayerMember(Payer payer, Member member) {
}
