package kr.ac.kookmin.stream.api.app.event.locker;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.event.locker.request.LockerSectionListParams;
import kr.ac.kookmin.stream.api.app.event.locker.response.LockerLayoutResponse;
import kr.ac.kookmin.stream.api.app.event.locker.response.LockerSectionListResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.event.domain.locker.service.LockerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/app/lockers")
@RequiredArgsConstructor
public class AppLockerController implements AppLockerApi {

    private final LockerService lockerService;

    @Override
    @GetMapping("/sections")
    public ApiResponse<LockerSectionListResponse> getSections(
        AppApiUser apiUser,
        @Valid @ModelAttribute LockerSectionListParams params
    ) {
        return ApiResponse.success(LockerSectionListResponse.from(
            lockerService.getSections(params.lockerPeriodId(), apiUser.userId())
        ));
    }

    @Override
    @GetMapping("/sections/{sectionId}")
    public ApiResponse<LockerLayoutResponse> getSectionLockers(
        AppApiUser apiUser,
        @PathVariable Long sectionId,
        @Valid @ModelAttribute LockerSectionListParams params
    ) {
        return ApiResponse.success(LockerLayoutResponse.from(
            lockerService.getSectionLockers(params.lockerPeriodId(), sectionId, apiUser.userId())
        ));
    }
}
