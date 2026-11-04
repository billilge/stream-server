package kr.ac.kookmin.stream.internal.domain.display.domain;

import java.time.LocalDate;
import java.util.List;

// 디스플레이 화면의 하루 칸. 그날에 걸친 학사 일정명을 시작일 → 등록 순으로 담고, 일정이 없는 날은 빈 목록이다.
public record DisplayDailySchedule(LocalDate date, List<String> titles) {
}
