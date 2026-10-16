package kr.ac.kookmin.stream.api.admin.event.locker;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.event.locker.request.LockerPeriodCreateRequest;
import kr.ac.kookmin.stream.api.admin.event.locker.request.LockerPeriodUpdateRequest;
import kr.ac.kookmin.stream.api.admin.event.locker.response.AdminLockerPeriodDetailResponse;
import kr.ac.kookmin.stream.api.admin.event.locker.response.AdminLockerPeriodListResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.event.domain.locker.service.LockerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/lockers/periods")
@RequiredArgsConstructor
public class AdminLockerPeriodController implements AdminLockerPeriodApi {

    private final LockerService lockerService;

    @Override
    @GetMapping
    public ApiResponse<AdminLockerPeriodListResponse> getLockerPeriods(AdminApiUser apiUser) {
        return ApiResponse.success(AdminLockerPeriodListResponse.from(lockerService.getAllPeriods()));
    }

    @Override
    @GetMapping("/{lockerPeriodId}")
    public ApiResponse<AdminLockerPeriodDetailResponse> getLockerPeriod(
        AdminApiUser apiUser,
        @PathVariable Long lockerPeriodId
    ) {
        return ApiResponse.success(
            AdminLockerPeriodDetailResponse.from(lockerService.getPeriodById(lockerPeriodId)));
    }

    @Override
    @PostMapping
    public ApiResponse<Void> createLockerPeriod(
        AdminApiUser apiUser,
        @Valid @RequestBody LockerPeriodCreateRequest request
    ) {
        lockerService.createPeriod(request.toCommand());
        return ApiResponse.success();
    }

    @Override
    @PutMapping("/{lockerPeriodId}")
    public ApiResponse<Void> updateLockerPeriod(
        AdminApiUser apiUser,
        @PathVariable Long lockerPeriodId,
        @Valid @RequestBody LockerPeriodUpdateRequest request
    ) {
        lockerService.updatePeriod(lockerPeriodId, request.toCommand());
        return ApiResponse.success();
    }

    @Override
    @DeleteMapping("/{lockerPeriodId}")
    public ApiResponse<Void> deleteLockerPeriod(AdminApiUser apiUser, @PathVariable Long lockerPeriodId) {
        lockerService.deletePeriod(lockerPeriodId);
        return ApiResponse.success();
    }
}
