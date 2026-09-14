package kr.ac.kookmin.stream.api.app.event.event;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.ApiResponse;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.event.event.request.EventApplyRequest;
import kr.ac.kookmin.stream.api.app.event.event.response.EventApplyResponse;
import kr.ac.kookmin.stream.api.app.event.event.response.EventFormResponse;
import kr.ac.kookmin.stream.event.domain.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/app/events")
@RequiredArgsConstructor
public class AppEventController {

    private final EventService eventService;

    @GetMapping("/{eventId}/form")
    public ApiResponse<EventFormResponse> getApplicationForm(@PathVariable Long eventId) {
        return ApiResponse.success(EventFormResponse.from(eventService.getApplicationForm(eventId)));
    }

    @PostMapping("/{eventId}/applications")
    public ApiResponse<EventApplyResponse> apply(
        AppApiUser apiUser,
        @PathVariable Long eventId,
        @Valid @RequestBody EventApplyRequest request
    ) {
        return ApiResponse.success(
            EventApplyResponse.from(eventService.apply(eventId, apiUser.userId(), request.toCommand()))
        );
    }
}
