package kr.ac.kookmin.stream.internal.domain.display.domain;

import java.time.LocalDate;

// 학사 일정 등록 요청. 하루 일정은 시작일과 종료일이 같다.
public record DisplayCalendarScheduleCreateCommand(String title, LocalDate startDate, LocalDate endDate) {
}
