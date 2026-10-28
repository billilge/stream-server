package kr.ac.kookmin.stream.api.admin.welfare.item;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.ac.kookmin.stream.api.admin.AdminApiUser;
import kr.ac.kookmin.stream.api.admin.welfare.item.request.ItemCreateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.item.request.ItemUpdateRequest;
import kr.ac.kookmin.stream.api.admin.welfare.item.response.AdminItemDetailResponse;
import kr.ac.kookmin.stream.api.common.dto.ApiResponse;
import kr.ac.kookmin.stream.api.common.dto.PageParams;
import kr.ac.kookmin.stream.api.common.dto.PageResponse;
import kr.ac.kookmin.stream.api.common.openapi.ApiErrorCode;
import kr.ac.kookmin.stream.common.CommonErrorCode;
import kr.ac.kookmin.stream.file.domain.FileErrorCode;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalErrorCode;
import org.springdoc.core.annotations.ParameterObject;

/**
 * 운영진 물품 API의 문서 명세. 구현은 {@link AdminItemController}가 맡는다.
 * <p>
 * 접근은 {@code ADMIN} role이면 된다. 부서 제한은 두지 않았다(PR 결정사항).
 */
@Tag(name = "물품 관리", description = "운영진 물품 목록 조회·등록·수정·삭제")
public interface AdminItemApi {

    /** 물품 목록. 카테고리·구분으로 거르고 이름으로 검색하며, 이름순으로 페이지 단위 조회한다. */
    @Operation(summary = "물품 목록 조회",
        description = "물품을 이름순으로 페이지 단위로 조회한다. category(ELECTRONICS, DAILY_SUPPLIES, MEDICINE, HYGIENE), "
            + "type(RENTAL, CONSUMABLE)을 주면 그 조건만, keyword를 주면 이름에 그 글자가 들어 있는 물품만 조회하며 "
            + "셋을 함께 쓸 수 있다. page는 0부터 시작한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    ApiResponse<PageResponse<AdminItemDetailResponse>> getItems(
        AdminApiUser apiUser,
        String category,
        String type,
        String keyword,
        @ParameterObject PageParams pageParams
    );

    /** 물품 등록. 이미지는 presigned URL로 먼저 업로드한 파일 ID로 받는다. */
    @Operation(summary = "물품 등록",
        description = "물품을 등록한다. 이미지는 POST /v1/files/presigned-url(category: ITEM_IMAGE)로 먼저 업로드한 "
            + "파일 ID(imageFileId)로 받는다. 이름이 이미 있으면 등록하지 않는다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = RentalErrorCode.class, codes = {"ITEM_NAME_DUPLICATED"})
    @ApiErrorCode(type = FileErrorCode.class, codes = {"FILE_NOT_FOUND"})
    ApiResponse<AdminItemDetailResponse> createItem(AdminApiUser apiUser, ItemCreateRequest request);

    /** 물품 수정. imageFileId를 생략하거나 null로 보내면 기존 이미지를 유지한다. */
    @Operation(summary = "물품 수정",
        description = "물품 정보(이름·카테고리·구분·재고·이미지·반납 정책)를 통째로 수정한다. imageFileId가 null이면 "
            + "기존 이미지를 유지한다.")
    @ApiErrorCode(type = CommonErrorCode.class, codes = {"INVALID_INPUT"})
    @ApiErrorCode(type = RentalErrorCode.class, codes = {"ITEM_NOT_FOUND", "ITEM_NAME_DUPLICATED"})
    @ApiErrorCode(type = FileErrorCode.class, codes = {"FILE_NOT_FOUND"})
    ApiResponse<AdminItemDetailResponse> updateItem(AdminApiUser apiUser, Long itemId, ItemUpdateRequest request);

    /** 물품 삭제. 하드 삭제이며 진행 중인 대여 기록이 있으면 막는다. */
    @Operation(summary = "물품 삭제",
        description = "물품을 삭제한다(하드 삭제). 진행 중인(아직 반납완료·대기취소·대여불가가 아닌) 대여 기록이 "
            + "있으면 삭제할 수 없다.")
    @ApiErrorCode(type = RentalErrorCode.class, codes = {"ITEM_NOT_FOUND", "ITEM_HAS_ACTIVE_RENTAL"})
    ApiResponse<Void> deleteItem(AdminApiUser apiUser, Long itemId);
}
