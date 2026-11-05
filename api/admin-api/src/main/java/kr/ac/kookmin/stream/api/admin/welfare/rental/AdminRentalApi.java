package kr.ac.kookmin.stream.api.admin.welfare.rental;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.welfare.rental.request.AdminRentalRecordCreateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.rental.request.AdminRentalStatusUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.rental.request.ItemCodeUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.rental.response.AdminRentalHistoryResponse;
import kr.ac.kookmin.stream.api.admin.welfare.rental.response.AdminRentalStatusWorkerLogResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.member.domain.member.domain.MemberErrorCode;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 운영진 대여 API의 문서 명세. 구현은 {@link AdminRentalController}가 맡는다.
 * <p>
 * 접근은 {@code ADMIN} role이면 된다. 부서 제한은 두지 않았다(PR 결정사항).
 */
@Tag(name = "대여 관리", description = "운영진 대여·반납 현황 조회, 임의 기록 추가")
public interface AdminRentalApi {

    /** 대여·반납 현황. 상태로 거르고 대여자 이름·학번으로 검색하며, 신청순으로 페이지 단위 조회한다. */
    @Operation(summary = "대여·반납 현황 조회",
        description = "모든 대여 기록을 신청 시각 내림차순으로 페이지 단위로 조회한다. keyword는 대여자 이름·학번 검색, "
            + "rentalStatus(PENDING, CONFIRMED, RENTAL, RETURN_PENDING, RETURN_CONFIRMED, RETURNED, CANCEL, "
            + "REJECTED)는 상태 필터이며 둘을 함께 쓸 수 있다.")
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
            + "폼에서 고른 값이고, 운영진(ADMIN role)이어야 한다. itemCode를 함께 보내면 등록과 동시에 물품 코드를 매긴다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT", "OPTIMISTIC_LOCK_CONFLICT"})
    @ApiErrorCode(type = RentalErrorCode.class, codes = {"ITEM_NOT_FOUND", "ITEM_OUT_OF_STOCK", "WORKER_NOT_ADMIN"})
    @ApiErrorCode(type = MemberErrorCode.class, codes = {"MEMBER_NOT_FOUND"})
    ApiResponse<AdminRentalHistoryResponse> createRentalRecord(AdminApiUser apiUser, AdminRentalRecordCreateRequest request);

    /** 대여 상태 변경. 전이 규칙 없이 어떤 값이든 허용하며, expectedStatus를 보내면 지금 상태가 같을 때만 바꾼다. 처리자는 호출한 관리자로 자동 기록된다. */
    @Operation(summary = "대여 상태 변경",
        description = "대여 기록의 상태를 직접 바꾼다. 전이 규칙은 없어 어떤 값으로도 바꿀 수 있다. 받아 가기 전 "
            + "상태(PENDING·CONFIRMED 등)에서 RENTAL로 바뀌면 그 시각을 실제 수령 시각으로, RETURNED로 바뀌면 그 시각을 "
            + "반납 완료 시각으로 기록한다(신청 시점에 학생이 고른 희망 시각과는 다르다). 반납 신청 반려처럼 받아 간 뒤의 "
            + "상태(RENTAL·RETURN_PENDING·RETURN_CONFIRMED·RETURNED)에서 RENTAL로 되돌릴 때는 수령 시각을 그대로 둔다. 소모품은 CONFIRMED로 요청해도 RENTAL을 거치지 않고 바로 RETURNED로 "
            + "전이한다(신청 시점에 이미 내준 걸 승인하는 것이라 수령 단계가 없다). 재고는 상태가 재고를 "
            + "차지하는지(진행 중인 상태, 그리고 이미 내준 소모품의 RETURNED)를 바꾸기 전후로 비교해, 차지하다 놓으면 "
            + "복구하고 놓았다 다시 차지하면 차감한다. 끝난 상태끼리 바꿨다 되돌려도 재고가 어긋나지 않는다. 처리자(workerId)는 호출한 관리자로 "
            + "자동 기록되며 운영진(ADMIN role)이어야 하고, 직전과 같은 상태로의 요청은 아무 효과가 없다(로그도 안 쌓인다). "
            + "expectedStatus(선택)에 화면에서 본 지금 상태를 보내면, 실제 지금 상태와 다를 때 RENTAL_STATUS_CHANGED(409)로 "
            + "거절하고 아무것도 바꾸지 않는다. 알림처럼 오래된 화면에서 누른 요청이 이미 처리된 기록을 다시 바꾸지 않게 할 때 쓴다. "
            + "보내지 않으면 지금 상태와 상관없이 바꾼다(기록 정정용).")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT", "OPTIMISTIC_LOCK_CONFLICT"})
    @ApiErrorCode(type = RentalErrorCode.class, codes = {"RENTAL_NOT_FOUND", "RENTAL_STATUS_CHANGED", "ITEM_OUT_OF_STOCK"})
    ApiResponse<AdminRentalHistoryResponse> updateStatus(
        AdminApiUser apiUser,
        Long rentalHistoryId,
        AdminRentalStatusUpdateRequest request
    );

    /** 대여 상태 변경 처리자 로그. 변경 시각 내림차순으로 조회한다. */
    @Operation(summary = "상태 변경 처리자 로그 조회",
        description = "대여 기록 하나의 상태 변경 처리자 로그를 변경 시각 내림차순으로 조회한다. 아직 누구도 상태를 바꾼 적이 "
            + "없으면 빈 배열이다.")
    @ApiErrorCode(type = RentalErrorCode.class, codes = {"RENTAL_NOT_FOUND"})
    ApiResponse<AdminRentalStatusWorkerLogResponse> getWorkerLogs(AdminApiUser apiUser, Long rentalHistoryId);

    /** 물품 코드 수정. 등록 뒤에 코드를 새로 매기거나 바꿀 때 쓴다. */
    @Operation(summary = "대여 기록 물품 코드 수정",
        description = "대여 기록 하나의 물품 코드를 바꾼다. itemCode를 null로 보내면 지운다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = RentalErrorCode.class, codes = {"RENTAL_NOT_FOUND"})
    ApiResponse<AdminRentalHistoryResponse> updateItemCode(
        AdminApiUser apiUser,
        Long rentalHistoryId,
        ItemCodeUpdateRequest request
    );

    /** 대여 기록 삭제. 잘못 등록한 기록을 지울 때 쓴다. */
    @Operation(summary = "대여 기록 삭제",
        description = "대여 기록을 하드 삭제한다. 그 기록이 재고를 차지하고 있었으면(진행 중인 상태, 그리고 이미 "
            + "내준 소모품의 RETURNED) 그만큼 물품 재고를 복구한다. 상태 변경 처리자 로그도 함께 지운다. 재고가 학생 신청 등으로 동시에 바뀌면 "
            + "서버가 몇 번 다시 시도하고, 그래도 충돌하면 409를 반환한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"OPTIMISTIC_LOCK_CONFLICT"})
    @ApiErrorCode(type = RentalErrorCode.class, codes = {"RENTAL_NOT_FOUND"})
    ApiResponse<Void> deleteRentalRecord(AdminApiUser apiUser, Long rentalHistoryId);
}
