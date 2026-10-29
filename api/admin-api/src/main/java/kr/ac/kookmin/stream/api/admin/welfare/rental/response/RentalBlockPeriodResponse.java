package kr.ac.kookmin.stream.api.admin.welfare.rental.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record RentalBlockPeriodResponse(
    @Schema(description = "이용 제한 시작일. 설정 전이면 null", example = "2026-12-01")
    LocalDate startDate,
    @Schema(description = "이용 제한 종료일. 설정 전이면 null", example = "2026-12-14")
    LocalDate endDate,
    @Schema(description = "이용 제한 사유. 설정 전이면 null", example = "기말고사 기간")
    String reason
) {

    public static RentalBlockPeriodResponse of(LocalDate startDate, LocalDate endDate, String reason) {
        return new RentalBlockPeriodResponse(startDate, endDate, reason);
    }
}
