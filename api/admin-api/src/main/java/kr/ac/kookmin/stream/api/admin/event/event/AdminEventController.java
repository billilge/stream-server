package kr.ac.kookmin.stream.api.admin.event.event;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.event.event.request.EventCreateRequest;
import kr.ac.kookmin.stream.api.admin.event.event.response.EventCreateResponse;
import kr.ac.kookmin.stream.api.admin.event.event.usecase.AdminEventWriteUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/events")
@RequiredArgsConstructor
public class AdminEventController implements AdminEventApi {

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
}
