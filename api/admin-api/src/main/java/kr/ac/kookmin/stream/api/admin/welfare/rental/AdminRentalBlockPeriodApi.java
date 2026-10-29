package kr.ac.kookmin.stream.api.admin.welfare.rental;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.welfare.rental.request.RentalBlockPeriodUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.rental.response.RentalBlockPeriodResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;

/**
 * 대여 이용 제한 기간 설정 API의 문서 명세. 구현은 {@link AdminRentalBlockPeriodController}가 맡는다.
 * <p>
 * 설정한 기간 동안은 학생의 대여 신청이 막힌다({@code RentalApplyUseCase} 참고). 접근은 {@code ADMIN} role이면
 * 된다(부서 제한 없음, 다른 billilge 관리자 API와 동일).
 */
@Tag(name = "이용 제한 기간 설정", description = "대여 신청을 막는 기간 조회·설정")
public interface AdminRentalBlockPeriodApi {

    /** 현재 설정된 이용 제한 기간. 설정 전이면 전부 null. */
    @Operation(summary = "이용 제한 기간 조회", description = "현재 설정된 이용 제한 기간을 조회한다. 아직 설정하지 않았으면 전부 null이다.")
    ApiResponse<RentalBlockPeriodResponse> getRentalBlockPeriod(AdminApiUser apiUser);

    /** 이용 제한 기간 설정. 종료일은 시작일보다 이전일 수 없다. */
    @Operation(summary = "이용 제한 기간 설정",
        description = "대여 신청을 막을 기간과 사유를 설정한다. 설정한 기간(시작일~종료일, 양 끝 포함) 동안 학생이 "
            + "대여를 신청하면 이 사유가 그대로 에러 메시지에 노출된다. 반납 신청·대여 취소는 이 기간에도 그대로 가능하다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    ApiResponse<RentalBlockPeriodResponse> updateRentalBlockPeriod(AdminApiUser apiUser, RentalBlockPeriodUpdateRequest request);
}
