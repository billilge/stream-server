package kr.ac.kookmin.stream.api.admin.internal.home;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.internal.home.request.AdminHomeApplySectionExposureRequest;
import kr.ac.kookmin.stream.api.admin.internal.home.request.AdminHomeApplySectionOrderUpdateRequest;
import kr.ac.kookmin.stream.api.admin.internal.home.request.AdminHomeApplySectionSearchParams;
import kr.ac.kookmin.stream.api.admin.internal.home.response.AdminHomeApplySectionListResponse;
import kr.ac.kookmin.stream.api.admin.internal.home.usecase.AdminHomeApplySectionUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.internal.domain.home.service.AdminHomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/home/apply-sections")
@RequiredArgsConstructor
public class AdminHomeApplySectionController implements AdminHomeApplySectionApi {

    private final AdminHomeService adminHomeService;
    private final AdminHomeApplySectionUseCase adminHomeApplySectionUseCase;

    @Override
    @GetMapping
    public ApiResponse<AdminHomeApplySectionListResponse> getApplySections(
        AdminApiUser apiUser,
        @Valid @ModelAttribute AdminHomeApplySectionSearchParams params
    ) {
        return ApiResponse.success(adminHomeApplySectionUseCase.getApplySections(params.isExposed()));
    }

    @Override
    @PutMapping("/exposure")
    public ApiResponse<Void> updateExposure(
        AdminApiUser apiUser,
        @Valid @RequestBody AdminHomeApplySectionExposureRequest request
    ) {
        adminHomeService.updateApplySectionExposure(request.toTarget(), request.isExposed());
        return ApiResponse.success();
    }

    @Override
    @PutMapping("/order")
    public ApiResponse<Void> updateOrders(
        AdminApiUser apiUser,
        @Valid @RequestBody AdminHomeApplySectionOrderUpdateRequest request
    ) {
        adminHomeService.updateApplySectionOrders(request.toTargets());
        return ApiResponse.success();
    }
}
