package kr.ac.kookmin.stream.api.admin.welfare.fee.usecase;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.service.PayerService;
import kr.ac.kookmin.stream.welfare.domain.fee.service.StudentTransferStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 납부 상태 처리. 납부 상태, 회원 쪽 사본, 납부자 명부 셋이 어긋나면 안 되므로 한 트랜잭션으로 묶는다.
 */
@Component
@RequiredArgsConstructor
public class AdminFeeReviewUseCase {

    private static final int MAX_ADMIN_NOTE_LENGTH = 255;

    private final StudentTransferStatusService studentTransferStatusService;
    private final PayerService payerService;
    private final MemberService memberService;

    @Transactional
    public StudentTransferStatus review(Long transferStatusId, TransferStatus status, String adminNote) {
        validateAdminNote(status, adminNote);

        StudentTransferStatus reviewed = studentTransferStatusService.review(transferStatusId, status, adminNote);
        Member member = memberService.getById(reviewed.getMemberId());

        memberService.changeFeeStatus(member.getId(), reviewed.getStatus().name());
        payerService.sync(member.getId(), member.getName(), member.getStudentId(), reviewed.getStatus());

        return reviewed;
    }

    // 확인필요는 학생에게 보여줄 안내 문구가 있어야 의미가 있다. 그 외 상태에서는 문구를 받아도 버린다
    private void validateAdminNote(TransferStatus status, String adminNote) {
        if (status != TransferStatus.NEEDS_CHECK) {
            return;
        }
        if (adminNote == null || adminNote.isBlank() || adminNote.length() > MAX_ADMIN_NOTE_LENGTH) {
            throw new BusinessException(FeeErrorCode.INVALID_ADMIN_NOTE);
        }
    }
}
