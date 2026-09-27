package kr.ac.kookmin.stream.api.app.event.locker;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.event.locker.request.LockerApplyRequest;
import kr.ac.kookmin.stream.api.app.event.locker.request.LockerPeriodParams;
import kr.ac.kookmin.stream.api.app.event.locker.response.LockerApplyResponse;
import kr.ac.kookmin.stream.api.app.event.locker.response.LockerSectionDetailResponse;
import kr.ac.kookmin.stream.api.app.event.locker.response.LockerSectionListResponse;
import kr.ac.kookmin.stream.api.app.event.locker.response.MyLockerApplicationListResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationSummary;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionSummary;
import kr.ac.kookmin.stream.event.domain.locker.service.LockerApplicationService;
import kr.ac.kookmin.stream.event.domain.locker.service.LockerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 학생 앱의 사물함 구역·배치 조회와 신청 API.
 * <p>
 * 선택 가능 여부와 내 사물함 표시는 조회 결과를 응답 DTO에서 맞춰봐서 만든다. 미게시 회차·없는 구역
 * 판정은 구역 조회가 하므로 신청 조회보다 먼저 호출해야 404가 앞선다.
 */
@RestController
@RequestMapping("/v1/app/lockers")
@RequiredArgsConstructor
public class AppLockerController implements AppLockerApi {

    private final LockerService lockerService;
    private final LockerApplicationService lockerApplicationService;

    @Override
    @GetMapping("/sections")
    public ApiResponse<LockerSectionListResponse> getSections(
        AppApiUser apiUser,
        @Valid @ModelAttribute LockerPeriodParams params
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
    public ApiResponse<LockerSectionDetailResponse> getSectionLockers(
        AppApiUser apiUser,
        @PathVariable Long sectionId,
        @Valid @ModelAttribute LockerPeriodParams params
    ) {
        Long lockerPeriodId = params.lockerPeriodId();
        List<Locker> lockers = lockerService.getSectionLockers(lockerPeriodId, sectionId);
        Set<Long> appliedLockerIds = lockerService.getAppliedLockerIds(lockerPeriodId);
        Long myLockerId = lockerService.getLockerByMemberId(lockerPeriodId, apiUser.userId())
            .map(Locker::getId)
            .orElse(null);

        return ApiResponse.success(LockerSectionDetailResponse.of(lockers, appliedLockerIds, myLockerId));
    }

    @Override
    @PostMapping("/applications")
    public ApiResponse<LockerApplyResponse> apply(
        AppApiUser apiUser,
        @Valid @RequestBody LockerApplyRequest request
    ) {
        return ApiResponse.success(
            LockerApplyResponse.from(lockerApplicationService.apply(apiUser.userId(), request.toCommand()))
        );
    }

    @Override
    @GetMapping("/applications")
    public ApiResponse<MyLockerApplicationListResponse> getMyApplications(AppApiUser apiUser) {
        List<LockerApplicationSummary> applications =
            lockerApplicationService.getApplicationsByMemberId(apiUser.userId());
        // 배정된 사물함이 없으면 빈 목록 대신 data를 비운다 (명세)
        return ApiResponse.success(applications.isEmpty() ? null : MyLockerApplicationListResponse.from(applications));
    }
}
