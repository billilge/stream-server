package kr.ac.kookmin.stream.api.app.welfare.fee;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.welfare.fee.request.FeeAmountParams;
import kr.ac.kookmin.stream.api.app.welfare.fee.request.FeeSubmitRequest;
import kr.ac.kookmin.stream.api.app.welfare.fee.response.FeeResponse;
import kr.ac.kookmin.stream.api.app.welfare.fee.usecase.FeeStatusUseCase;
import kr.ac.kookmin.stream.api.app.welfare.fee.usecase.FeeSubmitUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/app/fee")
@RequiredArgsConstructor
public class AppFeeController implements AppFeeApi {

    private final FeeStatusUseCase feeStatusUseCase;
    private final FeeSubmitUseCase feeSubmitUseCase;

    @Override
    @GetMapping("/me")
    public ApiResponse<FeeResponse> getFeeByMemberId(AppApiUser apiUser, @Valid @ModelAttribute FeeAmountParams params) {
        return ApiResponse.success(feeStatusUseCase.getMyFee(apiUser.userId(), params.remainingSemesters()));
    }

    @Override
    @PostMapping
    public ApiResponse<FeeResponse> submit(AppApiUser apiUser, @Valid @RequestBody FeeSubmitRequest request) {
        return ApiResponse.success(feeSubmitUseCase.submit(apiUser.userId(), request.toCommand()));
    }
}
