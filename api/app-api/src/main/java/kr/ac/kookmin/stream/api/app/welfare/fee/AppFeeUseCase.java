package kr.ac.kookmin.stream.api.app.welfare.fee;

import kr.ac.kookmin.stream.api.app.welfare.fee.response.FeeResponse;
import kr.ac.kookmin.stream.internal.domain.config.service.AdminConfigValueService;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeAmountCalculator;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeConfigKeys;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TossTransferLinkGenerator;
import kr.ac.kookmin.stream.welfare.domain.fee.service.StudentTransferStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppFeeUseCase {

    private final StudentTransferStatusService studentTransferStatusService;
    private final AdminConfigValueService adminConfigValueService;

    public FeeResponse getFeeByMemberId(Long memberId, int grade, int semester) {
        StudentTransferStatus transferStatus = studentTransferStatusService.getByMemberId(memberId);
        return FeeResponse.of(transferStatus, transferLinkUrl(grade, semester));
    }

    public FeeResponse requestConfirmation(Long memberId, int grade, int semester) {
        StudentTransferStatus transferStatus = studentTransferStatusService.requestConfirmation(memberId);
        return FeeResponse.of(transferStatus, transferLinkUrl(grade, semester));
    }

    // 계좌(은행/계좌번호)와 학기당 금액을 조회 시점에 조합해 링크를 만든다 — 계좌만 바뀌거나
    // 금액만 바뀌어도 항상 최신 값을 반영하기 위함(둘 다 admin_config_values에서 따로 관리, FeeConfigKeys 참고).
    // 실제 송금액은 남은 학기 수만큼만 걷는다(FeeAmountCalculator). 계좌가 아직 설정되지 않았으면 null을 반환한다.
    private String transferLinkUrl(int grade, int semester) {
        String bank = adminConfigValueService.getValue(FeeConfigKeys.TRANSFER_BANK).orElse(null);
        String accountNo = adminConfigValueService.getValue(FeeConfigKeys.TRANSFER_ACCOUNT_NO).orElse(null);
        if (bank == null || accountNo == null) {
            return null;
        }

        long amountPerSemester = adminConfigValueService.getValue(FeeConfigKeys.TRANSFER_AMOUNT)
            .map(Long::parseLong)
            .orElse(FeeConfigKeys.DEFAULT_TRANSFER_AMOUNT);
        long amount = FeeAmountCalculator.calculate(grade, semester, amountPerSemester);
        return TossTransferLinkGenerator.generate(bank, accountNo, amount);
    }
}
