package kr.ac.kookmin.stream.internal.domain.display.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayCalendarSchedule;

public interface DisplayCalendarScheduleRepository {

    Optional<DisplayCalendarSchedule> findById(Long id);

    // 기간이 startDate~endDate(양끝 포함)와 하루라도 겹치는 일정. 시작일 → 등록 순(id 오름차순)
    List<DisplayCalendarSchedule> findAllOverlapping(LocalDate startDate, LocalDate endDate);

    DisplayCalendarSchedule save(DisplayCalendarSchedule schedule);

    void deleteById(Long id);
}
