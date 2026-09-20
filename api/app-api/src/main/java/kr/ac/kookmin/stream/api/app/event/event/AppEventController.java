package kr.ac.kookmin.stream.api.app.event.event;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.ApiResponse;
import kr.ac.kookmin.stream.CursorSliceResponse;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.event.event.request.EventApplicationListRequest;
import kr.ac.kookmin.stream.api.app.event.event.request.EventApplyRequest;
import kr.ac.kookmin.stream.api.app.event.event.request.EventListRequest;
import kr.ac.kookmin.stream.api.app.event.event.response.EventApplicationDetailResponse;
import kr.ac.kookmin.stream.api.app.event.event.response.EventApplicationListItemResponse;
import kr.ac.kookmin.stream.api.app.event.event.response.EventApplyResponse;
import kr.ac.kookmin.stream.api.app.event.event.response.EventDetailResponse;
import kr.ac.kookmin.stream.api.app.event.event.response.EventFormResponse;
import kr.ac.kookmin.stream.api.app.event.event.response.EventListItemResponse;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationSummary;
import kr.ac.kookmin.stream.event.domain.event.domain.EventSummary;
import kr.ac.kookmin.stream.event.domain.event.service.EventApplicationService;
import kr.ac.kookmin.stream.event.domain.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 학생 앱의 행사 조회·신청 API.
 * <p>
 * 내 신청 내역은 {@code /v1/app/events/applications} 아래에 둔다. 같은 클래스의
 * {@code /v1/app/events/{eventId}}와 형태가 겹치지만 PathPatternParser가 리터럴 세그먼트를 경로 변수보다
 * 우선하므로 {@code applications}는 항상 아래 핸들러로 간다. {@code /v1/app/events/{문자열}} 형태를
 * 추가할 때는 이 우선순위를 확인해야 한다.
 */
@RestController
@RequestMapping("/v1/app/events")
@RequiredArgsConstructor
public class AppEventController implements AppEventApi {

    private final EventService eventService;
    private final EventApplicationService eventApplicationService;

    @GetMapping
    public ApiResponse<CursorSliceResponse<EventListItemResponse>> getEvents(
        @Valid @ModelAttribute EventListRequest request
    ) {
        CursorSliceResult<EventSummary> result = eventService.getPublishedEvents(
            request.toRecruitStatus(), request.toCursor(), request.sizeOrDefault());
        return ApiResponse.success(CursorSliceResponse.from(result, EventListItemResponse::from));
    }

    @Override
    @GetMapping("/applications")
    public ApiResponse<CursorSliceResponse<EventApplicationListItemResponse>> getMyApplications(
        AppApiUser apiUser,
        @Valid @ModelAttribute EventApplicationListRequest request
    ) {
        CursorSliceResult<EventApplicationSummary> result = eventApplicationService.getMyApplications(
            apiUser.userId(), request.toCursor(), request.sizeOrDefault());
        return ApiResponse.success(
            CursorSliceResponse.from(result, EventApplicationListItemResponse::from));
    }

    @Override
    @GetMapping("/applications/{applicationId}")
    public ApiResponse<EventApplicationDetailResponse> getMyApplication(
        AppApiUser apiUser,
        @PathVariable Long applicationId
    ) {
        return ApiResponse.success(EventApplicationDetailResponse.from(
            eventApplicationService.getMyApplication(applicationId, apiUser.userId())));
    }

    @Override
    @PatchMapping("/applications/{applicationId}/cancel")
    public ApiResponse<Void> cancelApplication(
        AppApiUser apiUser,
        @PathVariable Long applicationId
    ) {
        eventApplicationService.cancelApplication(applicationId, apiUser.userId());
        return ApiResponse.success();
    }

    @GetMapping("/{eventId}")
    public ApiResponse<EventDetailResponse> getEvent(@PathVariable Long eventId) {
        return ApiResponse.success(EventDetailResponse.from(eventService.getPublishedEvent(eventId)));
    }

    @GetMapping("/{eventId}/form")
    public ApiResponse<EventFormResponse> getApplicationForm(@PathVariable Long eventId) {
        return ApiResponse.success(EventFormResponse.from(eventApplicationService.getApplicationForm(eventId)));
    }

    @PostMapping("/{eventId}/applications")
    public ApiResponse<EventApplyResponse> apply(
        AppApiUser apiUser,
        @PathVariable Long eventId,
        @Valid @RequestBody EventApplyRequest request
    ) {
        return ApiResponse.success(
            EventApplyResponse.from(eventApplicationService.apply(eventId, apiUser.userId(), request.toCommand()))
        );
    }
}
