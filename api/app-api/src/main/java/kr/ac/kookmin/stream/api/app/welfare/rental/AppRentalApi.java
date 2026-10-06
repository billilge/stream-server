package kr.ac.kookmin.stream.api.app.welfare.rental;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.welfare.rental.request.ItemListParams;
import kr.ac.kookmin.stream.api.app.welfare.rental.request.RentalApplyRequest;
import kr.ac.kookmin.stream.api.app.welfare.rental.request.RentalHistoryListParams;
import kr.ac.kookmin.stream.api.app.welfare.rental.response.ItemListItemResponse;
import kr.ac.kookmin.stream.api.app.welfare.rental.response.RentalHistoryListResponse;
import kr.ac.kookmin.stream.api.app.welfare.rental.response.ReturnRequiredListResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.CursorSliceResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.FeeErrorCode;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 학생 앱 빌릴게(물품 대여) API의 문서 명세. 구현은 {@link AppRentalController}가 맡는다.
 * <p>
 * 스웨거 문서용 어노테이션만 이쪽에 두고 컨트롤러에는 라우팅과 본문만 남긴다. 경로 매핑과
 * 파라미터 바인딩(@{@code ModelAttribute} 등)은 구현체에 둔다.
 */
@Tag(name = "빌릴게", description = "학생 앱 빌릴게 물품·대여 이력 조회")
public interface AppRentalApi {

    /** 물품 목록. 이름순 커서 페이지네이션이며 카테고리·검색어로 거른다. */
    @Operation(summary = "물품 목록 조회",
        description = "물품을 이름순으로 커서 기반 조회한다. category로 분류를, keyword로 이름 검색을 거르고 "
            + "cursor/size로 다음 페이지를 넘긴다. 반납 정책(returnPolicy)은 대여품(RENTAL)에만 내려가고 소모품은 null이다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = RentalErrorCode.class, codes = {"ITEM_INVALID_CURSOR"})
    ApiResponse<CursorSliceResponse<ItemListItemResponse>> getItems(@ParameterObject ItemListParams params);

    /** 내 대여 이력. 최근 신청순이며 상태로 거른다. */
    @Operation(summary = "내 대여 이력 조회",
        description = "내 대여 이력을 최근 신청순으로 조회한다. status를 생략하면 전체 상태를 조회한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    ApiResponse<RentalHistoryListResponse> getHistories(
        AppApiUser apiUser,
        @ParameterObject RentalHistoryListParams params
    );

    /** 지금 반납해야 하는 대여와 반납 기한. */
    @Operation(summary = "반납 필요 대여 조회",
        description = "지금 반납해야 하는 대여(대여 중 상태)와 반납 기한(dueAt)을 조회한다. "
            + "반납 기한은 대여일에 최대 대여 일수를 더한 날의 반납 마감 시각이다. 없으면 빈 배열이다.")
    ApiResponse<ReturnRequiredListResponse> getReturnRequired(AppApiUser apiUser);

    /** 대여 신청. 회비 납부·재고·중복 대여·대여 가능 시간을 검증한 뒤 이력을 만든다. */
    @Operation(summary = "대여 신청",
        description = "물품을 대여 신청한다. 대여 시간은 영업시간(10~17시) 안이어야 하고 점심시간(12~13시)과 이미 지난 시각은 제외된다. "
            + "이미 대여 중인 같은 물품이 있으면 거부하되, ignoreDuplicate=true면 건너뛴다. "
            + "소모품은 신청 즉시 반납 완료로, 대여품은 대여 중으로 등록된다. "
            + "같은 물품에 신청이 몰려 재고 차감이 충돌하면 서버가 몇 번 다시 시도하고, 그래도 충돌하면 409를 반환한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT", "OPTIMISTIC_LOCK_CONFLICT"})
    @ApiErrorCode(type = FeeErrorCode.class, codes = {"MEMBER_IS_NOT_PAYER"})
    @ApiErrorCode(
        type = RentalErrorCode.class,
        codes = {
            "ITEM_NOT_FOUND", "ITEM_OUT_OF_STOCK", "RENTAL_ITEM_DUPLICATED",
            "INVALID_RENTAL_TIME_RANGE", "INVALID_RENTAL_TIME_LUNCH_BREAK", "INVALID_RENTAL_TIME_PAST"
        }
    )
    ApiResponse<Void> applyRental(AppApiUser apiUser, RentalApplyRequest request);

    /** 반납 신청. 본인 소유의 대여 중 이력만 대상이다. */
    @Operation(summary = "반납 신청", description = "대여 중인 물품의 반납을 신청한다. 신청 즉시 반납 완료로 처리된다.")
    @ApiErrorCode(type = RentalErrorCode.class, codes = {"RENTAL_NOT_FOUND"})
    ApiResponse<Void> returnRental(AppApiUser apiUser, Long rentalHistoryId);
}
