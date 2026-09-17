package kr.ac.kookmin.stream.api.admin.welfare.fee;

import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.service.StudentTransferStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminFeeReviewUseCase {

    private final StudentTransferStatusService studentTransferStatusService;
    private final MemberService memberService;

    // 확인요청 처리 + 납부자 명부(Payer) 반영을 하나의 트랜잭션으로 묶는다.
    @Transactional
    public StudentTransferStatus review(Long transferStatusId, TransferStatus status) {
        StudentTransferStatus transferStatus = studentTransferStatusService.review(transferStatusId, status);
        Member member = memberService.getById(transferStatus.getMemberId());
        studentTransferStatusService.syncPayer(
            member.getId(), member.getName(), member.getStudentId(), transferStatus.getStatus()
        );
        return transferStatus;
    }
}
