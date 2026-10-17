package kr.ac.kookmin.stream.api.admin.event.event;

import jakarta.validation.Valid;
import java.util.Map;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.event.event.request.EventCreateRequest;
import kr.ac.kookmin.stream.api.admin.event.event.request.EventSearchParams;
import kr.ac.kookmin.stream.api.admin.event.event.response.AdminEventDetailResponse;
import kr.ac.kookmin.stream.api.admin.event.event.response.AdminEventListItemResponse;
import kr.ac.kookmin.stream.api.admin.event.event.response.EventCreateResponse;
import kr.ac.kookmin.stream.api.admin.event.event.usecase.AdminEventWriteUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventManageDetail;
import kr.ac.kookmin.stream.event.domain.event.service.EventService;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
        return ApiResponse.success(PageResponse.from(
            eventService.getEvents(params.toKeyword(), pageParams.toOffset()), AdminEventListItemResponse::from));
    }

    @Override
    @GetMapping("/{eventId}")
    public ApiResponse<AdminEventDetailResponse> getEvent(AdminApiUser apiUser, @PathVariable Long eventId) {
        EventManageDetail detail = eventService.getEvent(eventId);
        Map<Long, File> imagesById = fileService.findAllByIdIn(detail.imageIds());

        return ApiResponse.success(AdminEventDetailResponse.from(detail, imagesById));
    }
}
