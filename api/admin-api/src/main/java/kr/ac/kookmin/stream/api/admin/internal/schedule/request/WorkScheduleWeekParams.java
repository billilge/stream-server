package kr.ac.kookmin.stream.api.admin.internal.schedule.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record WorkScheduleWeekParams(

    @Schema(description = "조회할 주에 속한 날짜. 토·일이면 그 앞 월요일의 주, 생략하면 이번 주", example = "2026-10-21")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate date
) {
}
