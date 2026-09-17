package kr.ac.kookmin.stream.api.admin.welfare.fee;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.ApiResponse;
import kr.ac.kookmin.stream.PageResponse;
import kr.ac.kookmin.stream.api.admin.welfare.fee.request.FeeLinkUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.fee.request.FeeStatusUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.AdminFeeSearchResponse;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.FeeLinkUpdateResponse;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.FeeStatusUpdateResponse;
import kr.ac.kookmin.stream.common.CouncilDepartment;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.internal.domain.config.service.ConfigService;
import kr.ac.kookmin.stream.security.DepartmentAccessChecker;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentFee;
import kr.ac.kookmin.stream.welfare.domain.fee.service.FeeService;
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

    private static final String FEE_TRANSFER_LINK_KEY = "FEE_TRANSFER_LINK";

    private final FeeService feeService;
    private final ConfigService configService;
    private final AdminFeeSearchUseCase adminFeeSearchUseCase;
    private final DepartmentAccessChecker departmentAccessChecker;

    @GetMapping("/requests")
    public ApiResponse<PageResponse<AdminFeeSearchResponse>> getRequests(
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        departmentAccessChecker.requireDepartment(CouncilDepartment.GENERAL_AFFAIRS);
        PageResult<AdminFeeSearchResponse> result = adminFeeSearchUseCase.search(status, keyword, page, size);
        return ApiResponse.success(PageResponse.from(result, response -> response));
    }

    @PatchMapping("/requests/{feeId}")
    public ApiResponse<FeeStatusUpdateResponse> updateStatus(
        @PathVariable Long feeId,
        @Valid @RequestBody FeeStatusUpdateRequest request
    ) {
        departmentAccessChecker.requireDepartment(CouncilDepartment.GENERAL_AFFAIRS);
        StudentFee fee = feeService.review(feeId, request.status());
        return ApiResponse.success(FeeStatusUpdateResponse.from(fee));
    }

    @PutMapping("/link")
    public ApiResponse<FeeLinkUpdateResponse> updateLink(@Valid @RequestBody FeeLinkUpdateRequest request) {
        departmentAccessChecker.requireDepartment(CouncilDepartment.GENERAL_AFFAIRS);
        configService.upsertValue(FEE_TRANSFER_LINK_KEY, request.transferLinkUrl());
        return ApiResponse.success(FeeLinkUpdateResponse.of(request.transferLinkUrl()));
    }
}
