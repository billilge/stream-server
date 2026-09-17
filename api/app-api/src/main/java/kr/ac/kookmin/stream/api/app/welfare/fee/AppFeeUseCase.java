package kr.ac.kookmin.stream.api.app.welfare.fee;

import kr.ac.kookmin.stream.api.app.welfare.fee.response.FeeResponse;
import kr.ac.kookmin.stream.internal.domain.config.service.ConfigService;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentFee;
import kr.ac.kookmin.stream.welfare.domain.fee.service.FeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppFeeUseCase {

    private static final String FEE_TRANSFER_LINK_KEY = "FEE_TRANSFER_LINK";

    private final FeeService feeService;
    private final ConfigService configService;

    public FeeResponse getMyFee(Long memberId) {
        StudentFee fee = feeService.getMyFee(memberId);
        return FeeResponse.of(fee, transferLinkUrl());
    }

    public FeeResponse requestConfirmation(Long memberId) {
        StudentFee fee = feeService.requestConfirmation(memberId);
        return FeeResponse.of(fee, transferLinkUrl());
    }

    private String transferLinkUrl() {
        return configService.getValue(FEE_TRANSFER_LINK_KEY).orElse(null);
    }
}
