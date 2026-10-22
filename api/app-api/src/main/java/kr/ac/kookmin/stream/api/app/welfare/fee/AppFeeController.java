package kr.ac.kookmin.stream.api.app.welfare.fee;

import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.welfare.fee.request.FeeSubmitRequest;
import kr.ac.kookmin.stream.api.app.welfare.fee.response.FeeResponse;
import kr.ac.kookmin.stream.api.app.welfare.fee.usecase.FeeSubmitUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.welfare.domain.fee.service.FeeAmountCalculator;
import kr.ac.kookmin.stream.welfare.domain.fee.service.StudentTransferStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/app/fee")
@RequiredArgsConstructor
public class AppFeeController implements AppFeeApi {

    private final StudentTransferStatusService studentTransferStatusService;
    private final FeeAccountProvider feeAccountProvider;
    private final FeeSubmitUseCase feeSubmitUseCase;

    // 남은 학기 수는 금액과 송금 링크를 계산할 때만 쓴다. 이 조회는 아무것도 저장하지 않는다
    @Override
    @GetMapping("/me")
    public ApiResponse<FeeResponse> getFeeByMemberId(
        AppApiUser apiUser,
        @RequestParam(required = false) Integer remainingSemesters
    ) {
        Integer semesters = remainingSemesters == null
            ? null
            : FeeAmountCalculator.validateRemainingSemesters(remainingSemesters);
        FeeAccount account = feeAccountProvider.getAccount();

        FeeResponse response = studentTransferStatusService.findLatestByMemberId(apiUser.userId())
            .map(transferStatus -> FeeResponse.of(transferStatus, account, semesters))
            .orElseGet(() -> FeeResponse.notRequested(account, semesters));
        return ApiResponse.success(response);
    }

    @Override
    @PostMapping
    public ApiResponse<FeeResponse> submit(AppApiUser apiUser, @RequestBody FeeSubmitRequest request) {
        return ApiResponse.success(feeSubmitUseCase.submit(apiUser.userId(), request));
    }
}
