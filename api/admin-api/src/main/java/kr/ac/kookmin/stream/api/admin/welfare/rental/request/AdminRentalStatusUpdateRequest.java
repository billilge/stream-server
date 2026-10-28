package kr.ac.kookmin.stream.api.admin.welfare.rental.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;

public record AdminRentalStatusUpdateRequest(
    @Schema(description = "바꿀 상태")
    @NotNull(message = "바꿀 상태를 입력해 주세요.")
    RentalStatus rentalStatus
) {
}
