package kr.ac.kookmin.stream.api.admin.event.locker;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.event.locker.request.LockerPeriodParams;
import kr.ac.kookmin.stream.api.admin.event.locker.response.AdminLockerApplicationSearchResponse;
import kr.ac.kookmin.stream.api.admin.event.locker.usecase.AdminLockerApplicationSearchUseCase;
import kr.ac.kookmin.stream.api.admin.event.locker.usecase.MemberLockerApplication;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.common.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/lockers/applications")
@RequiredArgsConstructor
public class AdminLockerApplicationController implements AdminLockerApplicationApi {

    private final AdminLockerApplicationSearchUseCase adminLockerApplicationSearchUseCase;

    @Override
    @GetMapping
    public ApiResponse<PageResponse<AdminLockerApplicationSearchResponse>> getApplications(
        AdminApiUser apiUser,
        @Valid @ModelAttribute LockerPeriodParams params,
        @RequestParam(required = false) String keyword,
        @Valid @ModelAttribute PageParams pageParams
    ) {
        PageResult<MemberLockerApplication> result = adminLockerApplicationSearchUseCase.search(
            params.lockerPeriodId(), keyword, pageParams.toOffset());

        return ApiResponse.success(PageResponse.from(result, vo -> AdminLockerApplicationSearchResponse.of(
            vo.result().application(), vo.result().locker(), vo.member())));
    }
}
