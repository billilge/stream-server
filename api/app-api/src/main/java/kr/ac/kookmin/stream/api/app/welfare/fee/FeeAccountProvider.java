package kr.ac.kookmin.stream.api.app.welfare.fee;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.internal.domain.config.service.AdminConfigValueService;
import kr.ac.kookmin.stream.welfare.WelfareConstants;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeAccount;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeConfigKeys;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 회비 납부 설정을 읽어 온다.
 * <p>
 * 계좌와 학기당 금액을 조회 시점에 함께 읽는다 — 계좌만 바뀌거나 금액만 바뀌어도 항상 최신 값을
 * 반영하기 위함(둘 다 admin_config_values에서 따로 관리, FeeConfigKeys 참고).
 * welfare 도메인은 internal 모듈에 의존할 수 없어 이 조회를 api에 둔다.
 */
@Component
@RequiredArgsConstructor
public class FeeAccountProvider {

    private final AdminConfigValueService adminConfigValueService;

    public FeeAccount getAccount() {
        String bank = getRequiredValue(FeeConfigKeys.TRANSFER_BANK);
        String accountNo = getRequiredValue(FeeConfigKeys.TRANSFER_ACCOUNT_NO);
        long amountPerSemester = adminConfigValueService.getValue(FeeConfigKeys.TRANSFER_AMOUNT)
            .map(Long::parseLong)
            .orElse(WelfareConstants.DEFAULT_TRANSFER_AMOUNT);

        return FeeAccount.of(bank, accountNo, amountPerSemester);
    }

    // 계좌가 설정되지 않았으면 송금 자체가 불가능하므로 막는다
    private String getRequiredValue(String configKey) {
        return adminConfigValueService.getValue(configKey)
            .orElseThrow(() -> new BusinessException(FeeErrorCode.FEE_ACCOUNT_NOT_FOUND));
    }
}
