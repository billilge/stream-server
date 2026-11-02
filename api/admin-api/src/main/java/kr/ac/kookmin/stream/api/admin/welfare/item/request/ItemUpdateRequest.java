package kr.ac.kookmin.stream.api.admin.welfare.item.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemType;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemUpdateCommand;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ReturnPolicy;

public record ItemUpdateRequest(
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

    @Schema(description = "재고 수량", example = "12")
    @NotNull(message = "재고 수량을 입력해 주세요.")
    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다.")
    Integer count,

    @Schema(description = "사전 업로드한 이미지 파일 ID. null이면 기존 이미지를 유지한다", example = "105")
    Long imageFileId,

    @Schema(description = "대여일부터 최대 대여 가능 일수. 대여품(RENTAL)만 의미 있고 소모품이면 무시된다. 0이면 당일 반납", example = "7")
    @Min(value = 0, message = "최대 대여 일수는 0 이상이어야 합니다.")
    Integer maxRentalDays
) {

    public ItemUpdateCommand toCommand(String imageKey) {
        return new ItemUpdateCommand(name, category, type, count, imageKey, maxRentalDays == null ? null : ReturnPolicy.of(maxRentalDays));
    }
}
