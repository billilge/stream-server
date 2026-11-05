package kr.ac.kookmin.stream.api.admin.welfare.rental.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;

public record AdminRentalStatusUpdateRequest(
    @Schema(description = "바꿀 상태")
    @NotNull(message = "바꿀 상태를 입력해 주세요.")
    RentalStatus rentalStatus,

    @Schema(description = "요청자가 본 지금 상태. 보내면 실제 지금 상태와 같을 때만 바꾸고, 다르면 RENTAL_STATUS_CHANGED로 "
        + "거절한다. 보내지 않으면 지금 상태와 상관없이 바꾼다(기록 정정용)", nullable = true)
    RentalStatus expectedStatus
) {
}
