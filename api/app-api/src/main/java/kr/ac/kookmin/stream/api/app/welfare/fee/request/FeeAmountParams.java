package kr.ac.kookmin.stream.api.app.welfare.fee.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * @param remainingSemesters 남은 학기 수. 주면 그 값으로 납부 금액과 송금 링크를 계산하고, 없으면 계산하지 않는다
 */
public record FeeAmountParams(
    @Min(value = 1, message = "남은 학기 수는 1 이상 8 이하여야 합니다.")
    @Max(value = 8, message = "남은 학기 수는 1 이상 8 이하여야 합니다.")
    Integer remainingSemesters
) {
}
