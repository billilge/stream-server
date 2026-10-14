package kr.ac.kookmin.stream.api.app.welfare.fee;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.internal.domain.config.service.AdminConfigValueService;
import kr.ac.kookmin.stream.welfare.WelfareConstants;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeConfigKeys;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;
import kr.ac.kookmin.stream.welfare.domain.fee.service.FeeAmountCalculator;
import kr.ac.kookmin.stream.welfare.domain.fee.service.TossTransferLinkGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 회비 납부 계좌 조회와 송금 링크 생성을 담당하는 파사드.
 * <p>
 * 계좌(은행/계좌번호)와 학기당 금액을 조회 시점에 조합한다 — 계좌만 바뀌거나 금액만 바뀌어도 항상 최신 값을
 * 반영하기 위함(둘 다 admin_config_values에서 따로 관리, FeeConfigKeys 참고).
 * welfare 도메인은 internal 모듈에 의존할 수 없어 이 조합을 api에 둔다.
 */
@Component
@RequiredArgsConstructor
public class FeeTransferUrlProvider {

    private final AdminConfigValueService adminConfigValueService;

    /**
     * @param remainingSemesters 남은 학기 수. null이면 금액과 링크를 만들지 않는다
     *                           (아직 제출 전이거나, 확인필요라 금액을 보여주지 않는 경우)
     */
    public FeeTransferInfo getTransferInfo(Integer remainingSemesters) {
        String bank = adminConfigValueService.getValue(FeeConfigKeys.TRANSFER_BANK).orElse(null);
        String accountNo = adminConfigValueService.getValue(FeeConfigKeys.TRANSFER_ACCOUNT_NO).orElse(null);

        if (bank == null || accountNo == null) {
            throw new BusinessException(FeeErrorCode.FEE_ACCOUNT_NOT_FOUND);
        }

        if (remainingSemesters == null) {
            return FeeTransferInfo.accountOnly(bank, accountNo);
        }

        long amountPerSemester = adminConfigValueService.getValue(FeeConfigKeys.TRANSFER_AMOUNT)
            .map(Long::parseLong)
            .orElse(WelfareConstants.DEFAULT_TRANSFER_AMOUNT);
        long transferAmount = FeeAmountCalculator.calculate(remainingSemesters, amountPerSemester);

        return new FeeTransferInfo(bank, accountNo, transferAmount,
            TossTransferLinkGenerator.generate(bank, accountNo, transferAmount));
    }
}
