package kr.ac.kookmin.stream.api.admin.internal.display;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.internal.display.request.DisplayCalendarScheduleCreateRequest;
import kr.ac.kookmin.stream.api.admin.internal.display.request.DisplayCalendarScheduleListParams;
import kr.ac.kookmin.stream.api.admin.internal.display.request.DisplayCalendarScheduleUpdateRequest;
import kr.ac.kookmin.stream.api.admin.internal.display.response.DisplayCalendarScheduleCreateResponse;
import kr.ac.kookmin.stream.api.admin.internal.display.response.DisplayCalendarScheduleListResponse;
import kr.ac.kookmin.stream.api.admin.internal.display.response.DisplayCalendarScheduleUpdateResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayCalendarSchedule;
import kr.ac.kookmin.stream.internal.domain.display.service.AdminDisplayCalendarScheduleService;
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
@RequestMapping("/v1/admin/display/calendar-schedules")
@RequiredArgsConstructor
public class AdminDisplayCalendarScheduleController implements AdminDisplayCalendarScheduleApi {

    private final AdminDisplayCalendarScheduleService adminDisplayCalendarScheduleService;

    @Override
    @GetMapping
    public ApiResponse<DisplayCalendarScheduleListResponse> getSchedules(
        AdminApiUser apiUser,
        @Valid @ModelAttribute DisplayCalendarScheduleListParams params
    ) {
        return ApiResponse.success(DisplayCalendarScheduleListResponse.from(
            adminDisplayCalendarScheduleService.getSchedules(params.startDate(), params.endDate())));
    }

    @Override
    @PostMapping
    public ApiResponse<DisplayCalendarScheduleCreateResponse> createSchedule(
        AdminApiUser apiUser,
        @Valid @RequestBody DisplayCalendarScheduleCreateRequest request
    ) {
        DisplayCalendarSchedule schedule = adminDisplayCalendarScheduleService.create(request.toCommand());
        return ApiResponse.success(DisplayCalendarScheduleCreateResponse.from(schedule));
    }

    @Override
    @PutMapping("/{scheduleId}")
    public ApiResponse<DisplayCalendarScheduleUpdateResponse> updateSchedule(
        AdminApiUser apiUser,
        @PathVariable Long scheduleId,
        @Valid @RequestBody DisplayCalendarScheduleUpdateRequest request
    ) {
        DisplayCalendarSchedule schedule = adminDisplayCalendarScheduleService.update(scheduleId, request.toCommand());
        return ApiResponse.success(DisplayCalendarScheduleUpdateResponse.from(schedule));
    }

    @Override
    @DeleteMapping("/{scheduleId}")
    public ApiResponse<Void> deleteSchedule(AdminApiUser apiUser, @PathVariable Long scheduleId) {
        adminDisplayCalendarScheduleService.delete(scheduleId);
        return ApiResponse.success();
    }
}
