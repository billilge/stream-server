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

    List<LockerPeriodJpaEntity> findAllByIsDeletedFalseOrderByApplyStartAtDesc();

    Optional<LockerPeriodJpaEntity> findByIdAndIsDeletedFalse(Long id);

    boolean existsByIdAndIsPublishedTrueAndIsDeletedFalse(Long id);

    Optional<LockerPeriodJpaEntity> findByIdAndIsPublishedTrueAndIsDeletedFalse(Long id);

    List<LockerPeriodJpaEntity> findAllByIdInAndIsPublishedTrueAndIsDeletedFalse(Collection<Long> ids);

    @Query("""
        SELECT p FROM LockerPeriodJpaEntity p
        WHERE p.isPublished = true
        AND p.isDeleted = false
        AND p.applyEndAt >= :from
        AND p.applyEndAt < :to
        """)
    List<LockerPeriodJpaEntity> findPublishedApplyEndingBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    List<LockerPeriodJpaEntity> findAllByIsPublishedTrueAndIsDeletedFalseAndUsageEndAt(LocalDate usageEndAt);
}
