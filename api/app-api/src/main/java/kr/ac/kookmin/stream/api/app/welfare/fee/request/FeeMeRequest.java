package kr.ac.kookmin.stream.api.app.welfare.fee.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record FeeMeRequest(
    @NotNull(message = "학년을 입력해 주세요.")
    @Min(value = 1, message = "학년은 1~4 사이여야 합니다.")
    @Max(value = 4, message = "학년은 1~4 사이여야 합니다.")
    Integer grade,

    @NotNull(message = "학기를 입력해 주세요.")
    @Min(value = 1, message = "학기는 1~2 사이여야 합니다.")
    @Max(value = 2, message = "학기는 1~2 사이여야 합니다.")
    Integer semester
) {
}
