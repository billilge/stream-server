package kr.ac.kookmin.stream.api.app.welfare.fee;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.internal.domain.config.service.AdminConfigValueService;
import kr.ac.kookmin.stream.welfare.WelfareConstants;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeConfigKeys;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.GradeSemester;
import kr.ac.kookmin.stream.welfare.domain.fee.service.FeeAmountCalculator;
import kr.ac.kookmin.stream.welfare.domain.fee.service.TossTransferLinkGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 회비 송금 링크 생성을 담당하는 파사드.
 * <p>
 * 계좌(은행/계좌번호)와 학기당 금액을 조회 시점에 조합해 링크를 만든다 — 계좌만 바뀌거나 금액만 바뀌어도
 * 항상 최신 값을 반영하기 위함(둘 다 admin_config_values에서 따로 관리, FeeConfigKeys 참고).
 * 실제 송금액은 남은 학기 수만큼만 걷는다(FeeAmountCalculator).
 */
@Component
@RequiredArgsConstructor
public class FeeTransferUrlProvider {

    private final AdminConfigValueService adminConfigValueService;

    // 계좌가 아직 설정되지 않았으면 링크를 만들 수 없으므로 예외를 던진다.
    public String getTransferUrl(GradeSemester gradeSemester) {
        String bank = adminConfigValueService.getValue(FeeConfigKeys.TRANSFER_BANK).orElse(null);
        String accountNo = adminConfigValueService.getValue(FeeConfigKeys.TRANSFER_ACCOUNT_NO).orElse(null);

        if (bank == null || accountNo == null) {
            throw new BusinessException(FeeErrorCode.FEE_TRANSFER_LINK_INVALID);
        }

        long amountPerSemester = adminConfigValueService.getValue(FeeConfigKeys.TRANSFER_AMOUNT)
            .map(Long::parseLong)
            .orElse(WelfareConstants.DEFAULT_TRANSFER_AMOUNT);

        long amount = FeeAmountCalculator.calculate(gradeSemester, amountPerSemester);

        return TossTransferLinkGenerator.generate(bank, accountNo, amount);
    }
}
