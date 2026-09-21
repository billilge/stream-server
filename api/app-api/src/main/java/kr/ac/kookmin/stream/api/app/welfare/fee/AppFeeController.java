package kr.ac.kookmin.stream.api.app.welfare.fee;

import kr.ac.kookmin.stream.ApiResponse;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.welfare.fee.request.FeeConfirmRequest;
import kr.ac.kookmin.stream.api.app.welfare.fee.response.FeeResponse;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.GradeSemester;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
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
    private final FeeTransferUrlProvider feeTransferUrlProvider;

    @Override
    @GetMapping("/me")
    public ApiResponse<FeeResponse> getFeeByMemberId(
        AppApiUser apiUser,
        @RequestParam(required = false) Integer grade,
        @RequestParam(required = false) Integer semester
    ) {
        GradeSemester gradeSemester = GradeSemester.of(grade, semester);
        StudentTransferStatus transferStatus = studentTransferStatusService.getByMemberId(apiUser.userId());

        return ApiResponse.success(FeeResponse.of(transferStatus, feeTransferUrlProvider.getTransferUrl(gradeSemester)));
    }

    @Override
    @PostMapping
    public ApiResponse<FeeResponse> requestConfirmation(
        AppApiUser apiUser,
        @RequestBody FeeConfirmRequest request
    ) {
        GradeSemester gradeSemester = request.toGradeSemester();
        StudentTransferStatus transferStatus = studentTransferStatusService.requestConfirmation(apiUser.userId());

        return ApiResponse.success(FeeResponse.of(transferStatus, feeTransferUrlProvider.getTransferUrl(gradeSemester)));
    }
}
