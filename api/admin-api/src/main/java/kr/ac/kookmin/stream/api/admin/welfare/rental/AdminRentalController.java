package kr.ac.kookmin.stream.api.admin.welfare.rental;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.welfare.rental.request.AdminRentalRecordCreateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.rental.response.AdminRentalHistoryResponse;
import kr.ac.kookmin.stream.api.admin.welfare.rental.usecase.AdminRentalRecordCreateUseCase;
import kr.ac.kookmin.stream.api.admin.welfare.rental.usecase.AdminRentalRecordView;
import kr.ac.kookmin.stream.api.admin.welfare.rental.usecase.AdminRentalSearchUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/billilge/histories")
@RequiredArgsConstructor
public class AdminRentalController implements AdminRentalApi {

    private final AdminRentalSearchUseCase adminRentalSearchUseCase;
    private final AdminRentalRecordCreateUseCase adminRentalRecordCreateUseCase;

    @Override
    @GetMapping
    public ApiResponse<PageResponse<AdminRentalHistoryResponse>> getRentalHistories(
        AdminApiUser apiUser,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String rentalStatus,
        @Valid @ModelAttribute PageParams pageParams
    ) {
        PageResult<AdminRentalRecordView> result = adminRentalSearchUseCase.search(
            RentalStatus.from(rentalStatus), keyword, pageParams.toOffset()
        );
        return ApiResponse.success(PageResponse.from(result, AdminRentalHistoryResponse::from));
    }

    @Override
    @PostMapping
    public ApiResponse<AdminRentalHistoryResponse> createRentalRecord(
        AdminApiUser apiUser,
        @Valid @RequestBody AdminRentalRecordCreateRequest request
    ) {
        AdminRentalRecordView view = adminRentalRecordCreateUseCase.create(request);
        return ApiResponse.success(AdminRentalHistoryResponse.from(view));
    }
}
