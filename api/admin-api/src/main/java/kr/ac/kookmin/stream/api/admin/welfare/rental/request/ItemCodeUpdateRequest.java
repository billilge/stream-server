package kr.ac.kookmin.stream.api.admin.welfare.rental.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ItemCodeUpdateRequest(
    @Schema(description = "물품 코드. null이면 지운다", example = "우산1")
    String itemCode
) {
}
