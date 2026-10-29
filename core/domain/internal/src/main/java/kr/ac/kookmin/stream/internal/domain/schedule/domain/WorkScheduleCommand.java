package kr.ac.kookmin.stream.internal.domain.schedule.domain;

import java.time.LocalDate;

// 근무 시간 등록·수정 요청 한 건. 근무자는 요청 단위 값이라 따로 받는다
public record WorkScheduleCommand(LocalDate workDate, int startHour, int endHour) {
}
