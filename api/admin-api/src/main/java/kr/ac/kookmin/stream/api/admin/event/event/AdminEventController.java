package kr.ac.kookmin.stream.api.admin.event.event;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.event.event.request.EventCreateRequest;
import kr.ac.kookmin.stream.api.admin.event.event.request.EventPublishRequest;
import kr.ac.kookmin.stream.api.admin.event.event.request.EventSearchParams;
import kr.ac.kookmin.stream.api.admin.event.event.request.EventUpdateRequest;
import kr.ac.kookmin.stream.api.admin.event.event.response.AdminEventDetailResponse;
import kr.ac.kookmin.stream.api.admin.event.event.response.AdminEventListItemResponse;
import kr.ac.kookmin.stream.api.admin.event.event.response.EventCreateResponse;
import kr.ac.kookmin.stream.api.admin.event.event.response.EventPublishResponse;
import kr.ac.kookmin.stream.api.admin.event.event.response.EventUpdateResponse;
import kr.ac.kookmin.stream.api.admin.event.event.usecase.AdminEventWriteUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.event.domain.EventManageDetail;
import kr.ac.kookmin.stream.event.domain.event.service.EventService;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/events")
@RequiredArgsConstructor
public class AdminEventController implements AdminEventApi {

    private final EventService eventService;
    private final FileService fileService;
    private final AdminEventWriteUseCase adminEventWriteUseCase;

    @Override
    @PostMapping
    public ApiResponse<EventCreateResponse> createEvent(
        AdminApiUser apiUser,
        @Valid @RequestBody EventCreateRequest request
    ) {
        Event event = adminEventWriteUseCase.create(apiUser.userId(), request.toCommand());
        return ApiResponse.success(EventCreateResponse.from(event));
    }

    @Override
    @GetMapping
    public ApiResponse<PageResponse<AdminEventListItemResponse>> getEvents(
        AdminApiUser apiUser,
        @Valid @ModelAttribute EventSearchParams params,
        @Valid @ModelAttribute PageParams pageParams
    ) {
        PageResult<Event> events = eventService.getEvents(params.toKeyword(), pageParams.toOffset());
        Map<Long, Long> applicantCounts = eventService.getApplicantCounts(
            events.content().stream().map(Event::getId).toList());
        LocalDateTime now = LocalDateTime.now();

        return ApiResponse.success(PageResponse.from(events, event ->
            AdminEventListItemResponse.of(event, applicantCounts.getOrDefault(event.getId(), 0L), now)));
    }

    @Override
    @GetMapping("/{eventId}")
    public ApiResponse<AdminEventDetailResponse> getEvent(AdminApiUser apiUser, @PathVariable Long eventId) {
        EventManageDetail detail = eventService.getEvent(eventId);
        Map<Long, File> imagesById = fileService.findAllByIdIn(detail.imageIds());

        return ApiResponse.success(AdminEventDetailResponse.of(detail, imagesById, LocalDateTime.now()));
    }

    @Override
    @PutMapping("/{eventId}")
    public ApiResponse<EventUpdateResponse> updateEvent(
        AdminApiUser apiUser,
        @PathVariable Long eventId,
        @Valid @RequestBody EventUpdateRequest request
    ) {
        Event event = adminEventWriteUseCase.update(eventId, request.toCommand());
        return ApiResponse.success(EventUpdateResponse.from(event));
    }

    @Override
    @PatchMapping("/{eventId}/publish")
    public ApiResponse<EventPublishResponse> changePublished(
        AdminApiUser apiUser,
        @PathVariable Long eventId,
        @Valid @RequestBody EventPublishRequest request
    ) {
        Event event = eventService.changePublished(eventId, request.isPublished());
        return ApiResponse.success(EventPublishResponse.from(event));
    }
}
