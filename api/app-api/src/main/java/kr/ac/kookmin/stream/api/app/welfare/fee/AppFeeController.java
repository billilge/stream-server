package kr.ac.kookmin.stream.api.app.welfare.fee;

import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.welfare.fee.request.FeeSubmitRequest;
import kr.ac.kookmin.stream.api.app.welfare.fee.response.FeeConfirmResponse;
import kr.ac.kookmin.stream.api.app.welfare.fee.response.FeeResponse;
import kr.ac.kookmin.stream.api.app.welfare.fee.response.FeeSubmitResponse;
import kr.ac.kookmin.stream.api.app.welfare.fee.usecase.FeeSubmitUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.service.StudentTransferStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/app/fee")
@RequiredArgsConstructor
public class AppFeeController implements AppFeeApi {

    private final StudentTransferStatusService studentTransferStatusService;
    private final FeeAccountProvider feeAccountProvider;
    private final FeeSubmitUseCase feeSubmitUseCase;

    @Override
    @GetMapping("/me")
    public ApiResponse<FeeResponse> getFeeByMemberId(AppApiUser apiUser) {
        StudentTransferStatus transferStatus = studentTransferStatusService.getByMemberId(apiUser.userId());

        return ApiResponse.success(FeeResponse.of(transferStatus, feeAccountProvider.getAccount()));
    }

    @Override
    @PostMapping
    public ApiResponse<FeeSubmitResponse> submit(AppApiUser apiUser, @RequestBody FeeSubmitRequest request) {
        return ApiResponse.success(feeSubmitUseCase.submit(apiUser.userId(), request));
    }

    @Override
    @PostMapping("/requests/confirm")
    public ApiResponse<FeeConfirmResponse> requestConfirmation(AppApiUser apiUser) {
        return ApiResponse.success(
            FeeConfirmResponse.from(studentTransferStatusService.requestConfirmation(apiUser.userId())));
    }
}
