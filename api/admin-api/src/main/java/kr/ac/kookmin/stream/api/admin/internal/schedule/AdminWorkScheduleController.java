package kr.ac.kookmin.stream.api.admin.internal.schedule;

import jakarta.validation.Valid;
import java.util.List;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.internal.schedule.request.WorkScheduleRegisterRequest;
import kr.ac.kookmin.stream.api.admin.internal.schedule.request.WorkScheduleUpdateRequest;
import kr.ac.kookmin.stream.api.admin.internal.schedule.request.WorkScheduleWeekParams;
import kr.ac.kookmin.stream.api.admin.internal.schedule.response.WorkScheduleResponse;
import kr.ac.kookmin.stream.api.admin.internal.schedule.response.WorkScheduleWeekResponse;
import kr.ac.kookmin.stream.api.admin.internal.schedule.response.WorkerResponse;
import kr.ac.kookmin.stream.api.admin.internal.schedule.usecase.AdminWorkScheduleUseCase;
import kr.ac.kookmin.stream.api.admin.internal.schedule.usecase.MemberWorkSchedule;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.internal.domain.schedule.service.AdminWorkScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/billilge/work-schedules")
@RequiredArgsConstructor
public class AdminWorkScheduleController implements AdminWorkScheduleApi {

    private final AdminWorkScheduleService adminWorkScheduleService;
    private final AdminWorkScheduleUseCase adminWorkScheduleUseCase;

    @Override
    @GetMapping
    public ApiResponse<WorkScheduleWeekResponse> getWorkSchedules(
        AdminApiUser apiUser,
        @Valid @ModelAttribute WorkScheduleWeekParams params
    ) {
        return ApiResponse.success(
            WorkScheduleWeekResponse.from(adminWorkScheduleUseCase.getWeek(params.date())));
    }

    @Override
    @GetMapping("/workers")
    public ApiResponse<List<WorkerResponse>> getWorkers(AdminApiUser apiUser) {
        return ApiResponse.success(adminWorkScheduleUseCase.getWorkers().stream().map(WorkerResponse::from).toList());
    }

    @Override
    @PostMapping
    public ApiResponse<List<WorkScheduleResponse>> registerWorkSchedules(
        AdminApiUser apiUser,
        @Valid @RequestBody WorkScheduleRegisterRequest request
    ) {
        List<MemberWorkSchedule> registered =
            adminWorkScheduleUseCase.register(request.memberId(), request.toCommands());
        return ApiResponse.success(registered.stream().map(WorkScheduleResponse::from).toList());
    }

    @Override
    @PutMapping("/{scheduleId}")
    public ApiResponse<WorkScheduleResponse> updateWorkSchedule(
        AdminApiUser apiUser,
        @PathVariable Long scheduleId,
        @Valid @RequestBody WorkScheduleUpdateRequest request
    ) {
        MemberWorkSchedule updated =
            adminWorkScheduleUseCase.update(scheduleId, request.memberId(), request.toCommand());
        return ApiResponse.success(WorkScheduleResponse.from(updated));
    }

    @Override
    @DeleteMapping("/{scheduleId}")
    public ApiResponse<Void> deleteWorkSchedule(AdminApiUser apiUser, @PathVariable Long scheduleId) {
        adminWorkScheduleService.delete(scheduleId);
        return ApiResponse.success();
    }
}
