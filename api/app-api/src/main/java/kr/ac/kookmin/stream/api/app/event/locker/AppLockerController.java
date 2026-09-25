package kr.ac.kookmin.stream.api.app.event.locker;

import jakarta.validation.Valid;
import java.util.List;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.event.locker.request.LockerSectionListParams;
import kr.ac.kookmin.stream.api.app.event.locker.response.LockerLayoutResponse;
import kr.ac.kookmin.stream.api.app.event.locker.response.LockerSectionListResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerAvailability;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionSummary;
import kr.ac.kookmin.stream.event.domain.locker.service.LockerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 학생 앱의 사물함 구역·배치 조회 API.
 * <p>
 * 미게시 회차·없는 구역 판정은 구역 조회가 하므로 신청 조회보다 먼저 호출해야 404가 앞선다.
 */
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
        Long lockerPeriodId = params.lockerPeriodId();
        List<LockerSectionSummary> sections = lockerService.getSections(lockerPeriodId);
        Long mySectionId = lockerService.getLockerByMemberId(lockerPeriodId, apiUser.userId())
            .map(Locker::getSectionId)
            .orElse(null);

        return ApiResponse.success(LockerSectionListResponse.of(sections, mySectionId));
    }

    @Override
    @GetMapping("/sections/{sectionId}")
    public ApiResponse<LockerLayoutResponse> getSectionLockers(
        AppApiUser apiUser,
        @PathVariable Long sectionId,
        @Valid @ModelAttribute LockerSectionListParams params
    ) {
        Long lockerPeriodId = params.lockerPeriodId();
        List<LockerAvailability> lockers = lockerService.getSectionLockers(lockerPeriodId, sectionId);
        Long myLockerId = lockerService.getLockerByMemberId(lockerPeriodId, apiUser.userId())
            .map(Locker::getId)
            .orElse(null);

        return ApiResponse.success(LockerLayoutResponse.of(lockers, myLockerId));
    }
}
