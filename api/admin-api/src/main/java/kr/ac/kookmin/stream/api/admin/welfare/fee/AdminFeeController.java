package kr.ac.kookmin.stream.api.admin.welfare.fee;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.security.RequireDepartment;
import kr.ac.kookmin.stream.api.admin.welfare.fee.request.FeeAccountUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.fee.request.FeeStatusUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.AdminFeeSearchResponse;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.FeeAccountResponse;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.FeeStatusUpdateResponse;
import kr.ac.kookmin.stream.api.admin.welfare.fee.usecase.AdminFeeReviewUseCase;
import kr.ac.kookmin.stream.api.admin.welfare.fee.usecase.AdminFeeSearchUseCase;
import kr.ac.kookmin.stream.api.admin.welfare.fee.usecase.MemberFeeStatus;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.common.CouncilDepartment;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeAccount;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
public class AdminFeeController implements AdminFeeApi {

    private final AdminFeeSearchUseCase adminFeeSearchUseCase;
    private final AdminFeeReviewUseCase adminFeeReviewUseCase;
    private final AdminFeeAccountProvider adminFeeAccountProvider;

    @Override
    @GetMapping("/requests")
    @RequireDepartment(CouncilDepartment.GENERAL_AFFAIRS)
    public ApiResponse<PageResponse<AdminFeeSearchResponse>> getRequests(
        AdminApiUser apiUser,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String keyword,
        @Valid @ModelAttribute PageParams pageParams
    ) {
        PageResult<MemberFeeStatus> result = adminFeeSearchUseCase.search(
            TransferStatus.from(status), keyword, pageParams.toOffset());

        return ApiResponse.success(PageResponse.from(result, AdminFeeSearchResponse::from));
    }

    @Override
    @PatchMapping("/requests/{studentTransferStatusId}")
    @RequireDepartment(CouncilDepartment.GENERAL_AFFAIRS)
    public ApiResponse<FeeStatusUpdateResponse> updateStatus(
        AdminApiUser apiUser,
        @PathVariable Long studentTransferStatusId,
        @Valid @RequestBody FeeStatusUpdateRequest request
    ) {
        StudentTransferStatus reviewed = adminFeeReviewUseCase.review(
            studentTransferStatusId, request.status(), request.rejectReason());

        return ApiResponse.success(FeeStatusUpdateResponse.from(reviewed));
    }

    @Override
    @GetMapping("/account")
    @RequireDepartment(CouncilDepartment.GENERAL_AFFAIRS)
    public ApiResponse<FeeAccountResponse> getAccount(AdminApiUser apiUser) {
        return ApiResponse.success(FeeAccountResponse.from(adminFeeAccountProvider.getAccount()));
    }

    @Override
    @PutMapping("/account")
    @RequireDepartment(CouncilDepartment.GENERAL_AFFAIRS)
    public ApiResponse<FeeAccountResponse> updateAccount(
        AdminApiUser apiUser,
        @Valid @RequestBody FeeAccountUpdateRequest request
    ) {
        FeeAccount saved = adminFeeAccountProvider.save(request.bank(), request.accountNo(), request.amount());

        return ApiResponse.success(FeeAccountResponse.from(saved));
    }
}
