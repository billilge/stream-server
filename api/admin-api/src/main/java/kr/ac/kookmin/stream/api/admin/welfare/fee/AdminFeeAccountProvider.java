package kr.ac.kookmin.stream.api.admin.welfare.fee;

import java.util.Map;
import kr.ac.kookmin.stream.internal.domain.config.service.AdminConfigValueService;
import kr.ac.kookmin.stream.welfare.WelfareConstants;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeBanks;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeConfigKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 회비 납부 설정을 읽고 쓴다. welfare 도메인은 internal 모듈에 의존할 수 없어 이 조합을 api에 둔다.
 * <p>
 * 학생 쪽과 달리 계좌가 없어도 예외를 던지지 않는다 — 관리자 화면은 "아직 설정하지 않음"을 보여주고
 * 그 자리에서 설정하게 해야 하기 때문이다.
 */
@Component
@RequiredArgsConstructor
public class AdminFeeAccountProvider {

    private final AdminConfigValueService adminConfigValueService;

    public FeeAccount getAccount() {
        return new FeeAccount(
            adminConfigValueService.getValue(FeeConfigKeys.TRANSFER_BANK).orElse(null),
            adminConfigValueService.getValue(FeeConfigKeys.TRANSFER_ACCOUNT_NO).orElse(null),
            adminConfigValueService.getValue(FeeConfigKeys.TRANSFER_AMOUNT)
                .map(Long::parseLong)
                .orElse(WelfareConstants.DEFAULT_TRANSFER_AMOUNT)
        );
    }

    // 계좌와 금액은 한 화면에서 같이 저장하므로 세 키를 한 번에 쓴다.
    // 은행명은 토스 송금 링크에 그대로 들어가므로 허용 목록에 있는 은행만 저장한다
    public FeeAccount save(String bank, String accountNo, long amount) {
        FeeBanks.validate(bank);
        adminConfigValueService.upsertValues(Map.of(
            FeeConfigKeys.TRANSFER_BANK, bank,
            FeeConfigKeys.TRANSFER_ACCOUNT_NO, accountNo,
            FeeConfigKeys.TRANSFER_AMOUNT, String.valueOf(amount)
        ));
        return new FeeAccount(bank, accountNo, amount);
    }
}
