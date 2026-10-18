package kr.ac.kookmin.stream.api.admin.event.locker;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.event.locker.request.LockerPeriodParams;
import kr.ac.kookmin.stream.api.admin.event.locker.request.LockerStatusChangeRequest;
import kr.ac.kookmin.stream.api.admin.event.locker.response.AdminLockerSectionDetailResponse;
import kr.ac.kookmin.stream.api.admin.event.locker.response.AdminLockerSectionListResponse;
import kr.ac.kookmin.stream.api.admin.event.locker.response.AdminLockerStatusChangeResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.event.domain.locker.domain.Locker;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionDetail;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerSectionSummary;
import kr.ac.kookmin.stream.event.domain.locker.service.AdminLockerService;
import kr.ac.kookmin.stream.event.domain.locker.service.LockerService;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/lockers/sections")
@RequiredArgsConstructor
public class AdminLockerSectionController implements AdminLockerSectionApi {

    private final LockerService lockerService;
    private final AdminLockerService adminLockerService;
    private final FileService fileService;

    @Override
    @GetMapping
    public ApiResponse<AdminLockerSectionListResponse> getLockerSections(
        AdminApiUser apiUser,
        @Valid @ModelAttribute LockerPeriodParams params
    ) {
        List<LockerSectionSummary> sections = lockerService.getSections(params.lockerPeriodId());
        return ApiResponse.success(AdminLockerSectionListResponse.from(sections));
    }

    @Override
    @GetMapping("/{sectionId}")
    public ApiResponse<AdminLockerSectionDetailResponse> getLockerSection(
        AdminApiUser apiUser,
        @PathVariable Long sectionId,
        @Valid @ModelAttribute LockerPeriodParams params
    ) {
        Long lockerPeriodId = params.lockerPeriodId();
        LockerSectionDetail detail = lockerService.getSectionDetail(lockerPeriodId, sectionId);
        // 배치 구조를 아직 등록하지 않은 구역이면 사진도 없다
        Map<Long, File> photosById = fileService.findAllByIdIn(
            detail.layout() == null ? List.of() : detail.layout().getPhotoFileIds());
        Set<Long> appliedLockerIds = lockerService.getAppliedLockerIds(lockerPeriodId);

        return ApiResponse.success(
            AdminLockerSectionDetailResponse.of(detail, photosById, appliedLockerIds));
    }

    @Override
    @PatchMapping("/{sectionId}/lockers/status")
    public ApiResponse<AdminLockerStatusChangeResponse> changeLockerStatus(
        AdminApiUser apiUser,
        @PathVariable Long sectionId,
        @Valid @RequestBody LockerStatusChangeRequest request
    ) {
        List<Locker> lockers = adminLockerService.changeLockerStatus(
            sectionId, request.lockerIds(), request.lockerStatus());
        return ApiResponse.success(AdminLockerStatusChangeResponse.from(lockers));
    }
}
