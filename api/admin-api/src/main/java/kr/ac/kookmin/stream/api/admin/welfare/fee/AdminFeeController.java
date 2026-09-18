package kr.ac.kookmin.stream.api.admin.welfare.fee;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.ApiResponse;
import kr.ac.kookmin.stream.PageResponse;
import kr.ac.kookmin.stream.api.admin.welfare.fee.request.FeeAmountUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.fee.request.FeeLinkUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.fee.request.FeeStatusUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.AdminFeeSearchResponse;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.FeeAmountUpdateResponse;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.FeeLinkUpdateResponse;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.FeeStatusUpdateResponse;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CouncilDepartment;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.internal.domain.config.service.AdminConfigValueService;
import kr.ac.kookmin.stream.security.DepartmentAccessChecker;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.Bank;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeConfigKeys;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/fee")
@RequiredArgsConstructor
public class AdminFeeController {

    private final AdminConfigValueService adminConfigValueService;
    private final AdminFeeSearchUseCase adminFeeSearchUseCase;
    private final AdminFeeReviewUseCase adminFeeReviewUseCase;
    private final DepartmentAccessChecker departmentAccessChecker;

    @GetMapping("/requests")
    public ApiResponse<PageResponse<AdminFeeSearchResponse>> getRequests(
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        departmentAccessChecker.requireDepartment(CouncilDepartment.GENERAL_AFFAIRS);
        PageResult<AdminFeeSearchResponse> result = adminFeeSearchUseCase.search(
            TransferStatus.from(status), keyword, page, size
        );
        return ApiResponse.success(PageResponse.from(result, response -> response));
    }

    @PatchMapping("/requests/{transferStatusId}")
    public ApiResponse<FeeStatusUpdateResponse> updateStatus(
        @PathVariable Long transferStatusId,
        @Valid @RequestBody FeeStatusUpdateRequest request
    ) {
        departmentAccessChecker.requireDepartment(CouncilDepartment.GENERAL_AFFAIRS);
        StudentTransferStatus transferStatus = adminFeeReviewUseCase.review(
            transferStatusId, TransferStatus.from(request.status())
        );
        return ApiResponse.success(FeeStatusUpdateResponse.from(transferStatus));
    }

    @PutMapping("/link")
    public ApiResponse<FeeLinkUpdateResponse> updateLink(@Valid @RequestBody FeeLinkUpdateRequest request) {
        departmentAccessChecker.requireDepartment(CouncilDepartment.GENERAL_AFFAIRS);
        Bank bank = Bank.from(request.bank());
        adminConfigValueService.upsertValue(FeeConfigKeys.TRANSFER_BANK, bank.displayName());
        adminConfigValueService.upsertValue(FeeConfigKeys.TRANSFER_ACCOUNT_NO, request.accountNo());
        return ApiResponse.success(FeeLinkUpdateResponse.of(bank.displayName(), request.accountNo()));
    }

    @PutMapping("/amount")
    public ApiResponse<FeeAmountUpdateResponse> updateAmount(@RequestBody FeeAmountUpdateRequest request) {
        departmentAccessChecker.requireDepartment(CouncilDepartment.GENERAL_AFFAIRS);
        if (request.amount() == null || request.amount() <= 0) {
            throw new BusinessException(FeeErrorCode.INVALID_FEE_AMOUNT);
        }
        adminConfigValueService.upsertValue(FeeConfigKeys.TRANSFER_AMOUNT, String.valueOf(request.amount()));
        return ApiResponse.success(FeeAmountUpdateResponse.of(request.amount()));
    }
}
