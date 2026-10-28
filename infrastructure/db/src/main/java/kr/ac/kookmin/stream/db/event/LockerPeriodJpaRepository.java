package kr.ac.kookmin.stream.db.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LockerPeriodJpaRepository extends JpaRepository<LockerPeriodJpaEntity, Long> {

    boolean existsByIdAndIsPublishedTrue(Long id);

    Optional<LockerPeriodJpaEntity> findByIdAndIsPublishedTrue(Long id);

    List<LockerPeriodJpaEntity> findAllByIdInAndIsPublishedTrue(Collection<Long> ids);

    @Query("""
        SELECT p FROM LockerPeriodJpaEntity p
        WHERE p.isPublished = true
        AND p.applyEndAt >= :from
        AND p.applyEndAt < :to
        """)
    List<LockerPeriodJpaEntity> findPublishedApplyEndingBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    List<LockerPeriodJpaEntity> findAllByIsPublishedTrueAndUsageEndAt(LocalDate usageEndAt);
}
