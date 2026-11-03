package kr.ac.kookmin.stream.api.admin.internal.schedule.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public record WorkScheduleWeekParams(

    @Schema(
        description = "조회할 주에 속한 날짜. 토·일이면 그 앞 월요일의 주, 생략하면 이번 주",
        type = "string",
        format = "date",
        example = "2026-10-21"
    )
    String date
) {

    // LocalDate로 바로 받으면 형식 오류 때 스프링 변환 메시지가 그대로 응답에 나가서 문자열로 받아 직접 바꾼다.
    // 형식이 틀리면 IllegalArgumentException → GlobalExceptionHandler가 INVALID_INPUT(400)으로 응답한다
    public LocalDate toDate() {
        if (date == null || date.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("날짜는 yyyy-MM-dd 형식이어야 합니다.");
        }
    }
}
