package kr.ac.kookmin.stream.api.app.internal.display;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.app.internal.display.request.DisplayCalendarScheduleListParams;
import kr.ac.kookmin.stream.api.app.internal.display.response.DisplayCalendarScheduleListResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.internal.domain.display.service.DisplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/app/calendar-schedules")
@RequiredArgsConstructor
public class AppDisplayCalendarScheduleController implements AppDisplayCalendarScheduleApi {

    private final DisplayService displayService;

    @Override
    @GetMapping
    public ApiResponse<DisplayCalendarScheduleListResponse> getSchedules(
        @Valid @ModelAttribute DisplayCalendarScheduleListParams params
    ) {
        return ApiResponse.success(
            DisplayCalendarScheduleListResponse.from(displayService.getSchedules(params.toPeriod())));
    }
}
