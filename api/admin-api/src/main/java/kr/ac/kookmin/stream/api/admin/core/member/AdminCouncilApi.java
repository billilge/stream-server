package kr.ac.kookmin.stream.api.admin.core.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.core.member.request.CouncilAssignRequest;
import kr.ac.kookmin.stream.api.admin.core.member.request.CouncilReleaseRequest;
import kr.ac.kookmin.stream.api.admin.core.member.request.CouncilUpdateRequest;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberErrorCode;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;

/**
 * 운영진 콘솔 관리자 관리 API의 문서 명세. 구현은 {@link AdminCouncilController}가 맡는다.
 * 명단 조회는 회원 목록 조회({@code GET /v1/admin/members})에 {@code councilOnly=true}로 한다.
 * <p>
 * 세 엔드포인트 모두 회장단(PRESIDENCY)만 호출할 수 있다. 다만 {@code RequireDepartmentAspect}의 규칙대로
 * 부서가 없는 운영진도 통과한다. 바꾼 결과 회장단 운영진이 한 명도 남지 않는 요청은 거부한다.
 */
@Tag(name = "관리자 관리", description = "운영진 콘솔 관리자 지정·권한 변경·해제")
public interface AdminCouncilApi {

    /** 회원들을 같은 학생회 부서의 운영진으로 지정한다. */
    @Operation(summary = "관리자 지정",
        description = "선택한 회원들을 ADMIN으로 올리고 같은 학생회 부서를 부여한다. 부서는 필수다 — 부서가 없는 "
            + "운영진은 모든 부서 권한을 가진 것으로 간주되기 때문이다. "
            + "운영진 화면이 학생회비 납부자 명부에서 회원을 고르는 구조라 납부자인지 서버도 확인한다. "
            + "이미 명단에 있는 회원이 하나라도 섞여 있으면 부서를 조용히 덮어쓰지 않도록 아무도 바꾸지 않는다. "
            + "이미 등록된 회원의 부서를 바꾸려면 권한·부서 변경을 쓴다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT", "FORBIDDEN"})
    @ApiErrorCode(type = FeeErrorCode.class, codes = {"MEMBER_IS_NOT_PAYER"})
    @ApiErrorCode(type = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND", "ALREADY_COUNCIL_MEMBER",
        "COUNCIL_DEPARTMENT_REQUIRED", "LAST_PRESIDENCY_ADMIN_REQUIRED"})
    ApiResponse<Void> assignCouncil(AdminApiUser apiUser, CouncilAssignRequest request);

    /** 권한을 바꾸고, 부서를 보냈으면 부서도 바꾼다. */
    @Operation(summary = "관리자 권한·부서 변경",
        description = "선택한 회원들의 권한을 바꾼다. councilDepartment를 함께 보내면 부서도 그 값으로 바꾸고, "
            + "보내지 않으면 각자의 지금 부서를 그대로 둔다 — 권한만 일괄로 바꾸는 화면이 부서를 보내지 않는다. "
            + "학생으로 내려도 부서가 남아 있으면 관리자 관리 명단에 그대로 보인다. 학생회 인원이지만 권한만 잠시 "
            + "내려두는 경우를 위한 것이다. "
            + "ADMIN으로 두면서 부서가 비어 있으면 거부한다. 이 API로는 부서를 비울 수 없고, 비우는 것은 해제가 맡는다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT", "FORBIDDEN"})
    @ApiErrorCode(type = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND", "COUNCIL_DEPARTMENT_REQUIRED",
        "LAST_PRESIDENCY_ADMIN_REQUIRED"})
    ApiResponse<Void> updateCouncil(AdminApiUser apiUser, CouncilUpdateRequest request);

    /** 명단에서 뺀다. */
    @Operation(summary = "관리자 해제",
        description = "선택한 회원들의 권한을 STUDENT로 되돌리고 학생회 부서를 비운다. 둘 다 비워지므로 관리자 관리 "
            + "명단에서 빠진다. 회원을 탈퇴시키는 것이 아니다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT", "FORBIDDEN"})
    @ApiErrorCode(type = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND", "LAST_PRESIDENCY_ADMIN_REQUIRED"})
    ApiResponse<Void> releaseCouncil(AdminApiUser apiUser, CouncilReleaseRequest request);
}
