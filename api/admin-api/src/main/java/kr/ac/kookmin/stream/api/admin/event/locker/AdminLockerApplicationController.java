package kr.ac.kookmin.stream.api.admin.event.locker;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.event.locker.request.LockerApplicationSearchParams;
import kr.ac.kookmin.stream.api.admin.event.locker.request.LockerAssignRequest;
import kr.ac.kookmin.stream.api.admin.event.locker.response.AdminLockerApplicationListResponse;
import kr.ac.kookmin.stream.api.admin.event.locker.usecase.AdminLockerApplicationSearchUseCase;
import kr.ac.kookmin.stream.api.admin.event.locker.usecase.AdminLockerAssignUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.event.domain.locker.service.AdminLockerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/lockers/applications")
@RequiredArgsConstructor
public class AdminLockerApplicationController implements AdminLockerApplicationApi {

    private final AdminLockerApplicationSearchUseCase adminLockerApplicationSearchUseCase;
    private final AdminLockerAssignUseCase adminLockerAssignUseCase;
    private final AdminLockerService adminLockerService;

    @Override
    @GetMapping
    public ApiResponse<AdminLockerApplicationListResponse> getApplications(
        AdminApiUser apiUser,
        @Valid @ModelAttribute LockerApplicationSearchParams params,
        @Valid @ModelAttribute PageParams pageParams
    ) {
        return ApiResponse.success(AdminLockerApplicationListResponse.from(
            adminLockerApplicationSearchUseCase.search(
                params.lockerPeriodId(), params.toKeyword(), params.toStatus(), pageParams.toOffset())));
    }

    @Override
    @DeleteMapping("/{lockerApplicationId}")
    public ApiResponse<Void> cancelApplication(AdminApiUser apiUser, @PathVariable Long lockerApplicationId) {
        adminLockerService.cancelApplication(lockerApplicationId);
        return ApiResponse.success();
    }

    @Override
    @PostMapping
    public ApiResponse<Void> assignLocker(AdminApiUser apiUser, @Valid @RequestBody LockerAssignRequest request) {
        adminLockerAssignUseCase.assign(request.lockerPeriodId(), request.memberId(), request.lockerId());
        return ApiResponse.success();
    }
}
