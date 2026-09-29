package kr.ac.kookmin.stream.api.app.welfare.rental;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.app.AppApiUser;
import kr.ac.kookmin.stream.api.app.welfare.rental.request.ItemListParams;
import kr.ac.kookmin.stream.api.app.welfare.rental.request.RentalHistoryListParams;
import kr.ac.kookmin.stream.api.app.welfare.rental.response.ItemListItemResponse;
import kr.ac.kookmin.stream.api.app.welfare.rental.response.RentalHistoryListResponse;
import kr.ac.kookmin.stream.api.app.welfare.rental.response.ReturnRequiredListResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.CursorSliceResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
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
}
