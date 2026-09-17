package kr.ac.kookmin.stream.api.admin.welfare.fee;

import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.service.FeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminFeeReviewUseCase {

    private final FeeService feeService;
    private final MemberService memberService;

    // 확인요청 처리 + 납부자 명부(Payer) 반영을 하나의 트랜잭션으로 묶는다.
    @Transactional
    public StudentTransferStatus review(Long feeId, String status) {
        StudentTransferStatus request = feeService.review(feeId, status);
        Member member = memberService.getById(request.getMemberId());
        feeService.syncPayer(member.getId(), member.getName(), member.getStudentId(), request.getStatus());
        return request;
    }
}
