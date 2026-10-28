package kr.ac.kookmin.stream.api.admin.welfare.item.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCreateCommand;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemType;

public record ItemCreateRequest(
    @Schema(description = "물품 이름", example = "우산")
    @NotBlank(message = "물품 이름을 입력해 주세요.")
    @Size(max = 255, message = "물품 이름은 255자 이하로 입력해 주세요.")
    String name,

    @Schema(description = "카테고리")
    @NotNull(message = "카테고리를 입력해 주세요.")
    ItemCategory category,

    @Schema(description = "물품 구분")
    @NotNull(message = "물품 구분을 입력해 주세요.")
    ItemType type,

    @Schema(description = "초기 재고 수량", example = "10")
    @NotNull(message = "재고 수량을 입력해 주세요.")
    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다.")
    Integer count,

    @Schema(description = "사전 업로드한 이미지 파일 ID", example = "101")
    @NotNull(message = "이미지 파일 ID를 입력해 주세요.")
    Long imageFileId,

    @Schema(description = "반납 정책. 대여품(RENTAL)이면 필수이고 소모품이면 무시된다")
    @Valid
    ReturnPolicyRequest returnPolicy
) {

    public ItemCreateCommand toCommand(String imageKey) {
        return new ItemCreateCommand(name, category, type, count, imageKey, returnPolicy == null ? null : returnPolicy.toDomain());
    }
}
