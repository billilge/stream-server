package kr.ac.kookmin.stream.api.admin.welfare.fee.usecase;

import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.service.PayerService;
import kr.ac.kookmin.stream.welfare.domain.fee.service.StudentTransferStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 납부 상태 처리. 상태 변경과 납부자 명부 반영이 어긋나면 안 되므로 한 트랜잭션으로 묶는다.
 * 서비스가 남기는 아웃박스 이벤트(납부 여부 변경)도 이 트랜잭션에 함께 커밋·롤백된다.
 */
@Component
@RequiredArgsConstructor
public class AdminFeeReviewUseCase {

    private final StudentTransferStatusService studentTransferStatusService;
    private final PayerService payerService;
    private final MemberService memberService;

    @Transactional
    public StudentTransferStatus review(Long transferStatusId, TransferStatus status, String adminNote) {
        StudentTransferStatus reviewed = studentTransferStatusService.review(transferStatusId, status, adminNote);
        Member member = memberService.getById(reviewed.getMemberId());

        payerService.sync(member.getId(), member.getName(), member.getStudentId(), reviewed.getStatus());

        return reviewed;
    }
}
