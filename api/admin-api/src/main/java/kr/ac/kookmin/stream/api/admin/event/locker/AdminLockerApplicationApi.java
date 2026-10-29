package kr.ac.kookmin.stream.api.admin.event.locker;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.event.locker.request.LockerAssignRequest;
import kr.ac.kookmin.stream.api.admin.event.locker.request.LockerPeriodParams;
import kr.ac.kookmin.stream.api.admin.event.locker.response.AdminLockerApplicationSearchResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerErrorCode;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 운영진 사물함 신청 API의 문서 명세. 구현은 {@link AdminLockerApplicationController}가 맡는다.
 * <p>
 * 접근은 {@code ADMIN} role이면 된다. 부서 제한은 두지 않았다.
 */
@Tag(name = "사물함 신청 관리", description = "운영진 사물함 신청 현황 조회·취소·수동 배정")
public interface AdminLockerApplicationApi {

    /** 신청자 이름·학번과 배정된 칸. 취소된 신청도 담는다. */
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

    @Operation(summary = "사물함 신청 취소",
        description = "운영진이 신청을 취소한다. 행은 남기고 취소 표시만 바꿔 같은 학생·같은 칸에 다시 배정할 수 있다. "
            + "학생 화면에서는 그 신청이 사라져 미신청으로 보이고, 취소됐다는 사실은 알림으로 전달한다. "
            + "사용 기간이 끝난 회차의 신청은 되돌릴 자리가 없어 거부한다.")
    @ApiErrorCode(type = LockerErrorCode.class,
        codes = {"LOCKER_APPLICATION_NOT_FOUND", "LOCKER_PERIOD_NOT_FOUND", "LOCKER_PERIOD_ALREADY_ENDED",
            "LOCKER_APPLICATION_ALREADY_CANCELED"})
    ApiResponse<Void> cancelApplication(AdminApiUser apiUser, Long lockerApplicationId);

    @Operation(summary = "사물함 수동 배정",
        description = "운영진이 학생회비 납부자에게 사물함을 직접 배정한다. 신청 기간은 보지 않는다 — 신청을 놓쳤거나 "
            + "자리를 옮겨야 하는 학생에게 쓰는 기능이라 기간이 끝난 뒤가 오히려 주 사용 시점이다. "
            + "사용 기간이 끝난 회차와 이미 신청된 칸은 거부하고, 한 학생이 한 회차에 두 칸을 가질 수 없다. "
            + "응답 본문이 없으므로 결과는 신청 현황을 다시 조회해 받는다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = FeeErrorCode.class, codes = {"MEMBER_IS_NOT_PAYER"})
    @ApiErrorCode(type = LockerErrorCode.class,
        codes = {"LOCKER_PERIOD_NOT_FOUND", "LOCKER_PERIOD_ALREADY_ENDED", "LOCKER_NOT_IN_SECTION",
            "LOCKER_ALREADY_ASSIGNED", "MEMBER_ALREADY_HAS_LOCKER"})
    ApiResponse<Void> assignLocker(AdminApiUser apiUser, LockerAssignRequest request);
}
