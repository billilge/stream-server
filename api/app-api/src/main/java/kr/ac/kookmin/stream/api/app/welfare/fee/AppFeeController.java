package kr.ac.kookmin.stream.api.app.welfare.fee;

import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.welfare.fee.request.FeeSubmitRequest;
import kr.ac.kookmin.stream.api.app.welfare.fee.response.FeeConfirmResponse;
import kr.ac.kookmin.stream.api.app.welfare.fee.response.FeeResponse;
import kr.ac.kookmin.stream.api.app.welfare.fee.response.FeeSubmitResponse;
import kr.ac.kookmin.stream.api.app.welfare.fee.usecase.FeeConfirmUseCase;
import kr.ac.kookmin.stream.api.app.welfare.fee.usecase.FeeSubmitUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;
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
    private final FeeTransferUrlProvider feeTransferUrlProvider;
    private final FeeSubmitUseCase feeSubmitUseCase;
    private final FeeConfirmUseCase feeConfirmUseCase;

    @Override
    @GetMapping("/me")
    public ApiResponse<FeeResponse> getFeeByMemberId(AppApiUser apiUser) {
        StudentTransferStatus transferStatus = studentTransferStatusService.getByMemberId(apiUser.userId());

        return ApiResponse.success(
            FeeResponse.of(transferStatus, feeTransferUrlProvider.getAccount(amountBaseOf(transferStatus))));
    }

    @Override
    @PostMapping
    public ApiResponse<FeeSubmitResponse> submit(AppApiUser apiUser, @RequestBody FeeSubmitRequest request) {
        return ApiResponse.success(feeSubmitUseCase.submit(apiUser.userId(), request));
    }

    @Override
    @PostMapping("/requests/confirm")
    public ApiResponse<FeeConfirmResponse> requestConfirmation(AppApiUser apiUser) {
        return ApiResponse.success(feeConfirmUseCase.confirm(apiUser.userId()));
    }

    /**
     * 금액·송금 링크를 만들 기준이 되는 남은 학기 수. 없으면 null이라 금액과 링크가 빠진다.
     * <p>
     * 확인필요는 아직 납부가 확인되지 않은 상태라 화면이 납부금액을 "-"로 두고, 송금 링크를 다시 주면
     * "또 보내라"로 읽히므로 함께 뺀다. 재제출은 관리자가 납부 전으로 되돌린 뒤에 열린다.
     */
    private Integer amountBaseOf(StudentTransferStatus transferStatus) {
        return transferStatus.getStatus() == TransferStatus.NEEDS_CHECK
            ? null
            : transferStatus.getRemainingSemesters();
    }
}
