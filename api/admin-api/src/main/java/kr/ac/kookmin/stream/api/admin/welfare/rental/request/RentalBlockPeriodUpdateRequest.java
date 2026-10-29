package kr.ac.kookmin.stream.api.admin.welfare.rental.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RentalBlockPeriodUpdateRequest(
    @Schema(description = "이용 제한 시작일", example = "2026-12-01")
    @NotNull(message = "시작일을 입력해 주세요.")
    LocalDate startDate,

    @Schema(description = "이용 제한 종료일", example = "2026-12-14")
    @NotNull(message = "종료일을 입력해 주세요.")
    LocalDate endDate,

    @Schema(description = "이용 제한 사유. 학생에게 그대로 노출된다", example = "기말고사 기간")
    @NotBlank(message = "이용 제한 사유를 입력해 주세요.")
    String reason
) {
}
