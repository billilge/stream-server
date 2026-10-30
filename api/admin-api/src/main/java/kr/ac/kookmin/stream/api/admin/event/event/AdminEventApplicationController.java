package kr.ac.kookmin.stream.api.admin.event.event;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.event.event.request.EventApplicationSearchParams;
import kr.ac.kookmin.stream.api.admin.event.event.response.AdminEventApplicationDetailResponse;
import kr.ac.kookmin.stream.api.admin.event.event.response.AdminEventApplicationListResponse;
import kr.ac.kookmin.stream.api.admin.event.event.usecase.AdminEventApplicationQueryUseCase;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/events/{eventId}/applications")
@RequiredArgsConstructor
public class AdminEventApplicationController implements AdminEventApplicationApi {

    private final AdminEventApplicationQueryUseCase adminEventApplicationQueryUseCase;

    @Override
    @GetMapping
    public ApiResponse<AdminEventApplicationListResponse> getApplications(
        AdminApiUser apiUser,
        @PathVariable Long eventId,
        @Valid @ModelAttribute EventApplicationSearchParams params,
        @Valid @ModelAttribute PageParams pageParams
    ) {
        return ApiResponse.success(AdminEventApplicationListResponse.from(adminEventApplicationQueryUseCase.getApplicants(
            eventId, params.toKeyword(), params.toStatus(), pageParams.toOffset())));
    }

    @Override
    @GetMapping("/{applicationId}")
    public ApiResponse<AdminEventApplicationDetailResponse> getApplication(
        AdminApiUser apiUser,
        @PathVariable Long eventId,
        @PathVariable Long applicationId
    ) {
        return ApiResponse.success(AdminEventApplicationDetailResponse.from(
            adminEventApplicationQueryUseCase.getApplicant(eventId, applicationId)));
    }
}
