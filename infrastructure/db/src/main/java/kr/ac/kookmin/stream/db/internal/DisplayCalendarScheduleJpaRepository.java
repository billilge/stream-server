package kr.ac.kookmin.stream.db.internal;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DisplayCalendarScheduleJpaRepository extends JpaRepository<DisplayCalendarScheduleJpaEntity, Long> {

    // 두 기간은 "내 시작이 상대 종료보다 늦지 않고, 내 종료가 상대 시작보다 이르지 않을 때" 겹친다
    @Query("""
        SELECT s FROM DisplayCalendarScheduleJpaEntity s
        WHERE s.startDate <= :endDate AND s.endDate >= :startDate
        ORDER BY s.startDate ASC, s.id ASC
        """)
    List<DisplayCalendarScheduleJpaEntity> findAllOverlapping(
        @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
