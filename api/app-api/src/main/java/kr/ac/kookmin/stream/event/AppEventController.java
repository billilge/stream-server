package kr.ac.kookmin.stream.event;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.ApiResponse;
import kr.ac.kookmin.stream.CursorCodec;
import kr.ac.kookmin.stream.CursorSliceResponse;
import kr.ac.kookmin.stream.app.AppApiUser;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.event.domain.event.domain.EventSummary;
import kr.ac.kookmin.stream.event.domain.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @GetMapping
    public ApiResponse<CursorSliceResponse<EventListItemResponse>> getEvents(
        @Valid @ModelAttribute EventListRequest request
    ) {
        CursorSliceResult<EventSummary> result = eventService.getPublishedEvents(
            request.toRecruitStatus(), request.toCursor(), request.sizeOrDefault());
        return ApiResponse.success(CursorSliceResponse.from(toResponse(result)));
    }

    @GetMapping("/{eventId}")
    public ApiResponse<EventDetailResponse> getEvent(@PathVariable Long eventId) {
        return ApiResponse.success(EventDetailResponse.from(eventService.getPublishedEvent(eventId)));
    }

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

    // 커서는 클라이언트에게 불투명한 토큰이어야 하므로 응답 직전 웹 계층에서 인코딩한다
    private CursorSliceResult<EventListItemResponse> toResponse(CursorSliceResult<EventSummary> result) {
        return new CursorSliceResult<>(
            result.content().stream().map(EventListItemResponse::from).toList(),
            result.hasNext(),
            result.nextCursor() == null ? null : CursorCodec.encode(result.nextCursor())
        );
    }
}
