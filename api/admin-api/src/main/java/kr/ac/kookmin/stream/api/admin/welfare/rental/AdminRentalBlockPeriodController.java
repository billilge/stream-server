package kr.ac.kookmin.stream.api.admin.welfare.rental;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.welfare.rental.request.RentalBlockPeriodUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.rental.response.RentalBlockPeriodResponse;
import kr.ac.kookmin.stream.api.admin.welfare.rental.usecase.AdminRentalBlockPeriodUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/billilge/rental-block-period")
@RequiredArgsConstructor
public class AdminRentalBlockPeriodController implements AdminRentalBlockPeriodApi {

    private final AdminRentalBlockPeriodUseCase adminRentalBlockPeriodUseCase;

    @Override
    @GetMapping
    public ApiResponse<RentalBlockPeriodResponse> getRentalBlockPeriod(AdminApiUser apiUser) {
        return ApiResponse.success(adminRentalBlockPeriodUseCase.getCurrent());
    }

    @Override
    @PutMapping
    public ApiResponse<RentalBlockPeriodResponse> updateRentalBlockPeriod(
        AdminApiUser apiUser,
        @Valid @RequestBody RentalBlockPeriodUpdateRequest request
    ) {
        return ApiResponse.success(adminRentalBlockPeriodUseCase.update(request));
    }
}
