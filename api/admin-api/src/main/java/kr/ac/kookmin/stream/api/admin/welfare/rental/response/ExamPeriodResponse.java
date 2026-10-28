package kr.ac.kookmin.stream.api.admin.welfare.rental.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record ExamPeriodResponse(
    @Schema(description = "시험기간 시작일. 설정 전이면 null", example = "2026-12-01")
    LocalDate startDate,
    @Schema(description = "시험기간 종료일. 설정 전이면 null", example = "2026-12-14")
    LocalDate endDate
) {

    public static ExamPeriodResponse of(LocalDate startDate, LocalDate endDate) {
        return new ExamPeriodResponse(startDate, endDate);
    }
}
