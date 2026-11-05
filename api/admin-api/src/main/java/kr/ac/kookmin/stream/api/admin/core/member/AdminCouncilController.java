package kr.ac.kookmin.stream.api.admin.core.member;

import jakarta.validation.Valid;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.core.member.request.CouncilAssignRequest;
import kr.ac.kookmin.stream.api.admin.core.member.request.CouncilReleaseRequest;
import kr.ac.kookmin.stream.api.admin.core.member.request.CouncilUpdateRequest;
import kr.ac.kookmin.stream.api.admin.core.member.usecase.AdminCouncilAssignUseCase;
import kr.ac.kookmin.stream.api.admin.security.RequireDepartment;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.common.CouncilDepartment;
import kr.ac.kookmin.stream.member.domain.member.service.AdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/members/council")
@RequiredArgsConstructor
public class AdminCouncilController implements AdminCouncilApi {

    private final AdminCouncilAssignUseCase adminCouncilAssignUseCase;
    private final AdminMemberService adminMemberService;

    @Override
    @PostMapping
    @RequireDepartment(CouncilDepartment.PRESIDENCY)
    public ApiResponse<Void> assignCouncil(AdminApiUser apiUser, @Valid @RequestBody CouncilAssignRequest request) {
        adminCouncilAssignUseCase.assign(request.toCommand());
        return ApiResponse.success();
    }

    @Override
    @PatchMapping
    @RequireDepartment(CouncilDepartment.PRESIDENCY)
    public ApiResponse<Void> updateCouncil(AdminApiUser apiUser, @Valid @RequestBody CouncilUpdateRequest request) {
        adminMemberService.updateCouncil(request.toCommand());
        return ApiResponse.success();
    }

    // 회원 행을 지우는 것이 아니라 권한·부서를 비우는 상태 변경이라 DELETE가 아니다. 저장소의 다른 일괄 상태
    // 변경(구역 사물함 상태, 아카이브 노출)과 같이 동작 이름을 하위 경로로 둔 PATCH로 받는다
    @Override
    @PatchMapping("/release")
    @RequireDepartment(CouncilDepartment.PRESIDENCY)
    public ApiResponse<Void> releaseCouncil(AdminApiUser apiUser, @Valid @RequestBody CouncilReleaseRequest request) {
        adminMemberService.releaseCouncil(request.distinctMemberIds());
        return ApiResponse.success();
    }
}
