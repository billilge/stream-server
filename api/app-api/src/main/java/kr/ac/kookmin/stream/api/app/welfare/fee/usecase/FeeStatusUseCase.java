package kr.ac.kookmin.stream.api.app.welfare.fee.usecase;

import java.util.Optional;
import kr.ac.kookmin.stream.api.app.welfare.fee.FeeAccountProvider;
import kr.ac.kookmin.stream.api.app.welfare.fee.response.FeeResponse;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeAccount;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.RemainingSemesters;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferGuide;
import kr.ac.kookmin.stream.welfare.domain.fee.service.StudentTransferStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 내 학생회비 납부 현황. 회비 납부 설정({@code internal})과 납부 요청({@code welfare})을 조합한다.
 * 아무것도 저장하지 않는다.
 */
@Component
@RequiredArgsConstructor
public class FeeStatusUseCase {

    private final StudentTransferStatusService studentTransferStatusService;
    private final FeeAccountProvider feeAccountProvider;

    public FeeResponse getMyFee(Long memberId, Integer remainingSemesters) {
        FeeAccount account = feeAccountProvider.getAccount();
        // 남은 학기 수는 선택값이라 여기서 한 번만 판단한다
        TransferGuide transferGuide = Optional.ofNullable(remainingSemesters)
            .map(RemainingSemesters::of)
            .map(account::createTransferGuide)
            .orElse(TransferGuide.notCalculated());

        return studentTransferStatusService.findLatestByMemberId(memberId)
            .map(transferStatus -> FeeResponse.of(transferStatus, account, transferGuide))
            .orElseGet(() -> FeeResponse.notRequested(account, transferGuide));
    }
}
