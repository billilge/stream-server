package kr.ac.kookmin.stream.api.admin.internal.display.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayCalendarScheduleCreateCommand;

public record DisplayCalendarScheduleCreateRequest(
    @Schema(description = "일정명", example = "학생회 간담회")
    @NotBlank(message = "일정명을 입력해 주세요.")
    @Size(max = 100, message = "일정명은 100자 이하로 입력해 주세요.")
    String title,

    @Schema(description = "시작일", example = "2026-10-05")
    @NotNull(message = "시작일을 입력해 주세요.")
    LocalDate startDate,

    @Schema(description = "종료일. 하루 일정이면 시작일과 같다", example = "2026-10-07")
    @NotNull(message = "종료일을 입력해 주세요.")
    LocalDate endDate
) {

    public DisplayCalendarScheduleCreateCommand toCommand() {
        return new DisplayCalendarScheduleCreateCommand(title, startDate, endDate);
    }
}
