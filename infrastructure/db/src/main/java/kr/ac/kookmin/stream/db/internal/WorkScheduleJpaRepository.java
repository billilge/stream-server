package kr.ac.kookmin.stream.db.internal;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkScheduleJpaRepository extends JpaRepository<WorkScheduleJpaEntity, Long> {

    @Query("""
        SELECT s FROM WorkScheduleJpaEntity s
        WHERE s.workDate BETWEEN :from AND :to
        ORDER BY s.workDate, s.startHour
        """)
    List<WorkScheduleJpaEntity> findAllByWorkDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    List<WorkScheduleJpaEntity> findAllByWorkDateIn(Collection<LocalDate> workDates);
}
