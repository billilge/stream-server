package kr.ac.kookmin.stream.api.admin.core.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.core.member.response.AdminMemberResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 운영진 콘솔 회원 조회 API의 문서 명세. 구현은 {@link AdminMemberController}가 맡는다.
 */
@Tag(name = "회원 관리", description = "운영진 콘솔 회원 목록 조회·검색")
public interface AdminMemberApi {

    /** 회원 목록. 이름순으로 페이지 단위로 조회한다. */
    @Operation(summary = "회원 목록 조회",
        description = "회원을 이름순으로 페이지 단위로 조회한다. keyword를 주면 이름·학번·이메일에 그 글자가 들어 있는 "
            + "회원만, department(AI, SW)를 주면 그 학부만, role(STUDENT, ADMIN)을 주면 그 역할만 조회하며 셋을 함께 "
            + "쓸 수 있다. 관리자 목록 화면은 role=ADMIN으로, 빌릴게 대여자 선택 등 일반 회원 검색은 role 없이 부른다. "
            + "로그인만 하고 가입을 마치지 않은 회원도 함께 조회되며, isSignedUp으로 구분한다. page는 0부터 시작한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    ApiResponse<PageResponse<AdminMemberResponse>> getMembers(
        AdminApiUser apiUser,
        String keyword,
        String department,
        String role,
        @ParameterObject PageParams pageParams
    );
}
