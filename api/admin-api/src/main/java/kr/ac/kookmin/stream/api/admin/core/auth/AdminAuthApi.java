package kr.ac.kookmin.stream.api.admin.core.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.core.auth.request.AdminLoginRequest;
import kr.ac.kookmin.stream.api.admin.core.auth.response.AdminLoginResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.auth.domain.admin.domain.AdminAuthErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;

/**
 * 운영진 인증 API의 문서 명세. 구현은 {@link AdminAuthController}가 맡는다.
 */
@Tag(name = "운영진 인증", description = "운영진 콘솔 로그인·공용 비밀번호 관리")
public interface AdminAuthApi {

    /** 학번 + 공용 비밀번호 로그인. 인증 없이 호출한다. */
    @Operation(summary = "운영진 로그인",
        description = """
            학번과 운영진 공용 비밀번호로 로그인한다. role이 ADMIN이고 학생회 부서가 있는 회원만 로그인할 수 있다.
            회원이 없거나, 운영진이 아니거나, 비밀번호가 틀리면 모두 INVALID_ADMIN_CREDENTIALS로 응답한다.""")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = AdminAuthErrorCode.class, codes = {"INVALID_ADMIN_CREDENTIALS"})
    ApiResponse<AdminLoginResponse> login(AdminLoginRequest request);
}
