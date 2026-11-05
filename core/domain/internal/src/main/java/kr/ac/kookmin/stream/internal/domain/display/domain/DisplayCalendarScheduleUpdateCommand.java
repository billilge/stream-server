package kr.ac.kookmin.stream.internal.domain.display.domain;

import java.time.LocalDate;

// 학사 일정 수정 요청. 일정명과 기간을 모두 받아 통째로 바꾼다.
public record DisplayCalendarScheduleUpdateCommand(String title, LocalDate startDate, LocalDate endDate) {
}
