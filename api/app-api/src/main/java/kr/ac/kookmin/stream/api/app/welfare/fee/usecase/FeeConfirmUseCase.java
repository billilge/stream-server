package kr.ac.kookmin.stream.api.app.welfare.fee.usecase;

import kr.ac.kookmin.stream.api.app.welfare.fee.response.FeeConfirmResponse;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.service.StudentTransferStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 송금 확인 요청. {@code welfare}의 납부 상태와 {@code member}의 사본을 함께 바꾼다.
 * 두 값이 어긋나면 관리자 목록과 상세가 다르게 보이므로 한 트랜잭션으로 묶는다.
 */
@Component
@RequiredArgsConstructor
public class FeeConfirmUseCase {

    private final StudentTransferStatusService studentTransferStatusService;
    private final MemberService memberService;

    @Transactional
    public FeeConfirmResponse confirm(Long memberId) {
        StudentTransferStatus requested = studentTransferStatusService.requestConfirmation(memberId);
        memberService.changeFeeStatus(memberId, requested.getStatus().name());

        return FeeConfirmResponse.from(requested);
    }
}
