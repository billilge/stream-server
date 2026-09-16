package kr.ac.kookmin.stream.api.app.welfare.fee;

import kr.ac.kookmin.stream.ApiResponse;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.welfare.fee.response.FeeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/app/fee")
@RequiredArgsConstructor
public class AppFeeController {

    private final AppFeeUseCase appFeeUseCase;

    @GetMapping("/me")
    public ApiResponse<FeeResponse> getMyFee(AppApiUser apiUser) {
        return ApiResponse.success(appFeeUseCase.getMyFee(apiUser.userId()));
    }

    @PostMapping
    public ApiResponse<FeeResponse> requestConfirmation(AppApiUser apiUser) {
        return ApiResponse.success(appFeeUseCase.requestConfirmation(apiUser.userId()));
    }
}
