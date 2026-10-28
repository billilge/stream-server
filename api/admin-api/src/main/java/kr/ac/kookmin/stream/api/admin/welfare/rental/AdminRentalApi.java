package kr.ac.kookmin.stream.api.admin.welfare.rental;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.welfare.rental.request.AdminRentalRecordCreateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.rental.response.AdminRentalHistoryResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 운영진 대여 API의 문서 명세. 구현은 {@link AdminRentalController}가 맡는다.
 * <p>
 * 접근은 {@code ADMIN} role이면 된다. 부서 제한은 두지 않았다(PR 결정사항).
 */
@Tag(name = "대여 관리", description = "운영진 대여·반납 현황 조회, 임의 기록 추가")
public interface AdminRentalApi {

    /** 대여·반납 현황. 상태로 거르고(OVERDUE 포함) 대여자 이름·학번으로 검색하며, 신청순으로 페이지 단위 조회한다. */
    @Operation(summary = "대여·반납 현황 조회",
        description = "모든 대여 기록을 신청 시각 내림차순으로 페이지 단위로 조회한다. keyword는 대여자 이름·학번 검색, "
            + "rentalStatus(PENDING, CONFIRMED, RENTAL, OVERDUE, RETURN_PENDING, RETURN_CONFIRMED, RETURNED, CANCEL, "
            + "REJECTED)는 상태 필터이며 둘을 함께 쓸 수 있다. OVERDUE(연체)는 별도 조회 없이 이 필터로 본다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    ApiResponse<PageResponse<AdminRentalHistoryResponse>> getRentalHistories(
        AdminApiUser apiUser,
        String keyword,
        String rentalStatus,
        @ParameterObject PageParams pageParams
    );

    /** 대여 기록 추가. 현장에서 처리한 대여를 관리자가 대신 등록하며, 등록과 동시에 재고를 차감한다. */
    @Operation(summary = "대여 기록 추가",
        description = "현장에서 수기로 처리한 대여를 관리자가 대신 등록한다. 등록 즉시 수령 완료로 보고 물품 재고를 "
            + "차감한다(소모품은 RETURNED, 대여품은 RENTAL로 시작). 근무자(workerId)는 호출한 관리자가 아니라 "
            + "폼에서 고른 값이다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT", "OPTIMISTIC_LOCK_CONFLICT"})
    @ApiErrorCode(
        type = RentalErrorCode.class,
        codes = {"ITEM_NOT_FOUND", "ITEM_OUT_OF_STOCK", "MEMBER_NOT_FOUND", "WORKER_NOT_FOUND"}
    )
    ApiResponse<AdminRentalHistoryResponse> createRentalRecord(AdminApiUser apiUser, AdminRentalRecordCreateRequest request);
}
