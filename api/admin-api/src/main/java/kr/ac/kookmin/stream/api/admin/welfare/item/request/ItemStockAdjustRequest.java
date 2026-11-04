package kr.ac.kookmin.stream.api.admin.welfare.item.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ItemStockAdjustRequest(
    @Schema(description = "재고 증감량. 양수면 늘리고 음수면 줄인다", example = "5")
    @NotNull(message = "증감량을 입력해 주세요.")
    Integer delta
) {
}
