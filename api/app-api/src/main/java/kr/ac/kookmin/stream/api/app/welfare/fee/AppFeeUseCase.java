package kr.ac.kookmin.stream.api.app.welfare.fee;

import kr.ac.kookmin.stream.api.app.welfare.fee.response.FeeResponse;
import kr.ac.kookmin.stream.internal.domain.config.service.ConfigService;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeConfigKeys;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TossTransferLinkGenerator;
import kr.ac.kookmin.stream.welfare.domain.fee.service.FeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppFeeUseCase {

    private final FeeService feeService;
    private final ConfigService configService;

    public FeeResponse getMyFee(Long memberId) {
        StudentTransferStatus status = feeService.getMyFee(memberId);
        return FeeResponse.of(status, transferLinkUrl());
    }

    public FeeResponse requestConfirmation(Long memberId) {
        StudentTransferStatus status = feeService.requestConfirmation(memberId);
        return FeeResponse.of(status, transferLinkUrl());
    }

    // 계좌(은행/계좌번호)와 금액을 조회 시점에 조합해 링크를 만든다 — 계좌만 바뀌거나
    // 금액만 바뀌어도 항상 최신 값을 반영하기 위함(둘 다 admin_config_values에서 따로 관리, FeeConfigKeys 참고).
    // 계좌가 아직 설정되지 않았으면 null을 반환한다.
    private String transferLinkUrl() {
        String bank = configService.getValue(FeeConfigKeys.TRANSFER_BANK).orElse(null);
        String accountNo = configService.getValue(FeeConfigKeys.TRANSFER_ACCOUNT_NO).orElse(null);
        if (bank == null || accountNo == null) {
            return null;
        }

        long amount = configService.getValue(FeeConfigKeys.TRANSFER_AMOUNT)
            .map(Long::parseLong)
            .orElse(FeeConfigKeys.DEFAULT_TRANSFER_AMOUNT);
        return TossTransferLinkGenerator.generate(bank, accountNo, amount);
    }
}
