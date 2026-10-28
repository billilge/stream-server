package kr.ac.kookmin.stream.api.admin.event.locker;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.event.locker.request.LockerPeriodParams;
import kr.ac.kookmin.stream.api.admin.event.locker.response.AdminLockerApplicationSearchResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 운영진 사물함 신청 API의 문서 명세. 구현은 {@link AdminLockerApplicationController}가 맡는다.
 * <p>
 * 접근은 {@code ADMIN} role이면 된다. 부서 제한은 두지 않았다.
 */
@Tag(name = "사물함 신청 관리", description = "운영진 사물함 신청 현황 조회")
public interface AdminLockerApplicationApi {

    /** 운영 회차의 신청 현황. */
    @Operation(summary = "사물함 신청 현황 조회",
        description = "운영 회차의 신청을 신청 일시 최신순으로 조회한다. 이름·학번 일부를 keyword로 넘기면 그 회원만 본다. "
            + "운영진이 취소한 신청도 담기며 canceled로 구분한다. 학생 화면에서는 취소된 신청이 보이지 않는다. "
            + "탈퇴한 회원의 지난 신청은 name·studentId가 null이고, 철거된 사물함은 lockerLabel이 null이다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = LockerErrorCode.class, codes = {"LOCKER_PERIOD_NOT_FOUND"})
    ApiResponse<PageResponse<AdminLockerApplicationSearchResponse>> getApplications(
        AdminApiUser apiUser,
        @ParameterObject LockerPeriodParams params,
        String keyword,
        @ParameterObject PageParams pageParams
    );
}
