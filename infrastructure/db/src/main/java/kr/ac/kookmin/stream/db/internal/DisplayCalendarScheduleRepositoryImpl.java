package kr.ac.kookmin.stream.db.internal;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayCalendarSchedule;
import kr.ac.kookmin.stream.internal.domain.display.repository.DisplayCalendarScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DisplayCalendarScheduleRepositoryImpl implements DisplayCalendarScheduleRepository {

    private final DisplayCalendarScheduleJpaRepository displayCalendarScheduleJpaRepository;

    @Override
    public Optional<DisplayCalendarSchedule> findById(Long id) {
        return displayCalendarScheduleJpaRepository.findById(id).map(DisplayCalendarScheduleJpaEntity::toDomain);
    }

    @Override
    public List<DisplayCalendarSchedule> findAllOverlapping(LocalDate startDate, LocalDate endDate) {
        return displayCalendarScheduleJpaRepository.findAllOverlapping(startDate, endDate).stream()
            .map(DisplayCalendarScheduleJpaEntity::toDomain)
            .toList();
    }

    @Override
    public DisplayCalendarSchedule save(DisplayCalendarSchedule schedule) {
        return displayCalendarScheduleJpaRepository.save(DisplayCalendarScheduleJpaEntity.from(schedule)).toDomain();
    }

    @Override
    public void deleteById(Long id) {
        displayCalendarScheduleJpaRepository.deleteById(id);
    }
}
