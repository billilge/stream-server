package kr.ac.kookmin.stream.db.internal;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.internal.domain.schedule.domain.WorkSchedule;
import kr.ac.kookmin.stream.internal.domain.schedule.repository.WorkScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WorkScheduleRepositoryImpl implements WorkScheduleRepository {

    private final WorkScheduleJpaRepository workScheduleJpaRepository;

    @Override
    public Optional<WorkSchedule> findById(Long id) {
        return workScheduleJpaRepository.findById(id).map(WorkScheduleJpaEntity::toDomain);
    }

    @Override
    public List<WorkSchedule> findAllByWorkDateBetween(LocalDate from, LocalDate to) {
        return workScheduleJpaRepository.findAllByWorkDateBetween(from, to).stream()
            .map(WorkScheduleJpaEntity::toDomain)
            .toList();
    }

    @Override
    public List<WorkSchedule> findAllByWorkDateIn(Collection<LocalDate> workDates) {
        return workScheduleJpaRepository.findAllByWorkDateIn(workDates).stream()
            .map(WorkScheduleJpaEntity::toDomain)
            .toList();
    }

    @Override
    public List<WorkSchedule> saveAll(List<WorkSchedule> workSchedules) {
        return workScheduleJpaRepository.saveAll(workSchedules.stream().map(WorkScheduleJpaEntity::from).toList())
            .stream()
            .map(WorkScheduleJpaEntity::toDomain)
            .toList();
    }

    @Override
    public WorkSchedule save(WorkSchedule workSchedule) {
        return workScheduleJpaRepository.save(WorkScheduleJpaEntity.from(workSchedule)).toDomain();
    }

    @Override
    public void deleteById(Long id) {
        workScheduleJpaRepository.deleteById(id);
    }
}
