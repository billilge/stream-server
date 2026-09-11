package kr.ac.kookmin.stream.event;

import kr.ac.kookmin.stream.ApiResponse;
import kr.ac.kookmin.stream.event.domain.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}
