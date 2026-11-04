package kr.ac.kookmin.stream.internal.domain.schedule.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.internal.domain.schedule.domain.WorkSchedule;

public interface WorkScheduleRepository {
    Optional<WorkSchedule> findById(Long id);
    // from~to(양끝 포함) 근무일의 근무 시간. 근무일, 시작 시각 오름차순
    List<WorkSchedule> findAllByWorkDateBetween(LocalDate from, LocalDate to);
    List<WorkSchedule> findAllByWorkDateIn(Collection<LocalDate> workDates);
    List<WorkSchedule> saveAll(List<WorkSchedule> workSchedules);
    WorkSchedule save(WorkSchedule workSchedule);
    void deleteById(Long id);
}
