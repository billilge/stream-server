package kr.ac.kookmin.stream.api.admin.internal.display.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplaySchedulePeriod;
import org.springframework.format.annotation.DateTimeFormat;

public record DisplayCalendarScheduleListParams(

    @Schema(description = "조회 시작일. 기간이 startDate~endDate와 하루라도 겹치는 일정을 조회한다", example = "2026-10-01")
    @NotNull(message = "조회 시작일을 입력해 주세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate startDate,

    @Schema(description = "조회 종료일. 시작일과 같거나 이후여야 한다", example = "2026-10-31")
    @NotNull(message = "조회 종료일을 입력해 주세요.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate endDate
) {

    // 시작일·종료일이 뒤바뀐 요청(예: 10/31~10/1)은 일정 기간과 같은 에러로 거절한다.
    // 그대로 조회하면 겹침 조건이 뒤집혀 범위 전체를 덮는 일정만 걸려 나온다
    public DisplaySchedulePeriod toPeriod() {
        return DisplaySchedulePeriod.of(startDate, endDate);
    }
}
