package kr.ac.kookmin.stream.api.admin.welfare.rental.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record ItemCodeUpdateRequest(
    @Schema(description = "물품 코드. null이면 지운다", example = "우산1")
    @Size(max = 255, message = "물품 코드는 255자 이하로 입력해 주세요.")
    String itemCode
) {
}
