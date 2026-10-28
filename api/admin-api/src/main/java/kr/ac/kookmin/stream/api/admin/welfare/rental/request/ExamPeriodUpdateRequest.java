package kr.ac.kookmin.stream.api.admin.welfare.rental.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ExamPeriodUpdateRequest(
    @Schema(description = "시험기간 시작일", example = "2026-12-01")
    @NotNull(message = "시작일을 입력해 주세요.")
    LocalDate startDate,

    @Schema(description = "시험기간 종료일", example = "2026-12-14")
    @NotNull(message = "종료일을 입력해 주세요.")
    LocalDate endDate
) {
}
