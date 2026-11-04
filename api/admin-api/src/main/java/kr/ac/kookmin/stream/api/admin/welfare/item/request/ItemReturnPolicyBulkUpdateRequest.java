package kr.ac.kookmin.stream.api.admin.welfare.item.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ItemReturnPolicyBulkUpdateRequest(
    @Schema(description = "반납 정책을 바꿀 물품 ID 목록", example = "[1, 2, 3]")
    @NotEmpty(message = "물품을 하나 이상 선택해 주세요.")
    List<Long> itemIds,

    @Schema(description = "대여일부터 최대 대여 가능 일수. 0이면 당일 반납", example = "7")
    @NotNull(message = "최대 대여 일수를 입력해 주세요.")
    @Min(value = 0, message = "최대 대여 일수는 0 이상이어야 합니다.")
    Integer maxRentalDays
) {
}
