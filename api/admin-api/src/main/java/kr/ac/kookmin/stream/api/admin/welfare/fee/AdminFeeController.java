package kr.ac.kookmin.stream.api.admin.welfare.fee;

import jakarta.validation.Valid;
import java.util.Map;
import kr.ac.kookmin.stream.ApiResponse;
import kr.ac.kookmin.stream.PageParams;
import kr.ac.kookmin.stream.PageResponse;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.security.RequireDepartment;
import kr.ac.kookmin.stream.api.admin.welfare.fee.request.FeeAmountUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.fee.request.FeeLinkUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.fee.request.FeeStatusUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.AdminFeeSearchResponse;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.FeeAmountUpdateResponse;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.FeeLinkUpdateResponse;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.FeeStatusUpdateResponse;
import kr.ac.kookmin.stream.api.admin.welfare.fee.usecase.AdminFeeReviewUseCase;
import kr.ac.kookmin.stream.api.admin.welfare.fee.usecase.AdminFeeSearchUseCase;
import kr.ac.kookmin.stream.api.admin.welfare.fee.usecase.MemberFeeStatus;
import kr.ac.kookmin.stream.common.CouncilDepartment;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.internal.domain.config.service.AdminConfigValueService;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeConfigKeys;
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

    private final AdminConfigValueService adminConfigValueService;
    private final AdminFeeSearchUseCase adminFeeSearchUseCase;
    private final AdminFeeReviewUseCase adminFeeReviewUseCase;

    @Override
    @GetMapping("/requests")
    @RequireDepartment(CouncilDepartment.GENERAL_AFFAIRS)
    public ApiResponse<PageResponse<AdminFeeSearchResponse>> getRequests(
        AdminApiUser apiUser,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String keyword,
        @Valid @ModelAttribute PageParams pageParams
    ) {
        TransferStatus transferStatus = TransferStatus.from(status);
        PageResult<MemberFeeStatus> result = adminFeeSearchUseCase.search(transferStatus, keyword, pageParams.toOffset());

        return ApiResponse.success(
                PageResponse.from(result, (vo) -> AdminFeeSearchResponse.of(vo.status(), vo.member()))
        );
    }

    @Override
    @PatchMapping("/requests/{transferStatusId}")
    @RequireDepartment(CouncilDepartment.GENERAL_AFFAIRS)
    public ApiResponse<FeeStatusUpdateResponse> updateStatus(
        AdminApiUser apiUser,
        @PathVariable Long transferStatusId,
        @Valid @RequestBody FeeStatusUpdateRequest request
    ) {
        TransferStatus status = TransferStatus.from(request.status());
        StudentTransferStatus reviewed = adminFeeReviewUseCase.review(transferStatusId, status);

        return ApiResponse.success(FeeStatusUpdateResponse.from(reviewed));
    }

    @Override
    @PutMapping("/link")
    @RequireDepartment(CouncilDepartment.GENERAL_AFFAIRS)
    public ApiResponse<FeeLinkUpdateResponse> updateLink(
        AdminApiUser apiUser,
        @Valid @RequestBody FeeLinkUpdateRequest request
    ) {
        adminConfigValueService.upsertValues(Map.of(
            FeeConfigKeys.TRANSFER_BANK, request.bank(),
            FeeConfigKeys.TRANSFER_ACCOUNT_NO, request.accountNo()
        ));

        return ApiResponse.success(FeeLinkUpdateResponse.of(request.bank(), request.accountNo()));
    }

    @Override
    @PutMapping("/amount")
    @RequireDepartment(CouncilDepartment.GENERAL_AFFAIRS)
    public ApiResponse<FeeAmountUpdateResponse> updateAmount(
        AdminApiUser apiUser,
        @Valid @RequestBody FeeAmountUpdateRequest request
    ) {
        adminConfigValueService.upsertValue(FeeConfigKeys.TRANSFER_AMOUNT, String.valueOf(request.amount()));
        return ApiResponse.success(FeeAmountUpdateResponse.of(request.amount()));
    }
}
