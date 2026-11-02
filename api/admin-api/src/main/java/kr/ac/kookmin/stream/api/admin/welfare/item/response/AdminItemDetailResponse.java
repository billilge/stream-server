package kr.ac.kookmin.stream.api.admin.welfare.item.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.format.DateTimeFormatter;
import kr.ac.kookmin.stream.api.common.StorageUrlBuilder;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemType;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ReturnPolicy;

/** 물품 목록 조회·등록·수정 응답에서 공통으로 쓰는 물품 상세. 별도 물품 상세 조회 API가 없어 등록·수정 응답이 그 역할을 겸한다. */
public record AdminItemDetailResponse(
    @Schema(description = "물품 ID", example = "13")
    Long itemId,
    @Schema(description = "물품 이름", example = "우산")
    String name,
    @Schema(description = "카테고리")
    ItemCategory category,
    @Schema(description = "물품 구분")
    ItemType type,
    @Schema(description = "재고 수량", example = "10")
    int count,
    @Schema(description = "이미지 URL")
    String imageUrl,
    @Schema(description = "반납 정책. 소모품이거나 정책이 없으면 null")
    ReturnPolicyResponse returnPolicy
) {

    public static AdminItemDetailResponse from(Item item) {
        ReturnPolicy returnPolicy = item.getReturnPolicy();
        return new AdminItemDetailResponse(
            item.getId(),
            item.getName(),
            item.getCategory(),
            item.getType(),
            item.getCount(),
            StorageUrlBuilder.build(item.getImageKey()),
            returnPolicy == null ? null : ReturnPolicyResponse.from(returnPolicy)
        );
    }

    public record ReturnPolicyResponse(int maxRentalDays, String returnDeadline) {

        private static final DateTimeFormatter DEADLINE_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

        public static ReturnPolicyResponse from(ReturnPolicy returnPolicy) {
            return new ReturnPolicyResponse(returnPolicy.maxRentalDays(), returnPolicy.returnDeadline().format(DEADLINE_FORMAT));
        }
    }
}
