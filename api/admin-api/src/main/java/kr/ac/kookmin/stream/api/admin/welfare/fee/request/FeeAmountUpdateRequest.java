package kr.ac.kookmin.stream.api.admin.welfare.fee.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record FeeAmountUpdateRequest(

    @NotNull(message = "유효하지 않은 금액입니다.")
    @Positive(message = "유효하지 않은 금액입니다.")
    Integer amount
) {
}
