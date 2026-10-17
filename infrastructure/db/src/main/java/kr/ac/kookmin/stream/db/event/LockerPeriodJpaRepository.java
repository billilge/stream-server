package kr.ac.kookmin.stream.db.event;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LockerPeriodJpaRepository extends JpaRepository<LockerPeriodJpaEntity, Long> {

    List<LockerPeriodJpaEntity> findAllByIsDeletedFalseOrderByApplyStartAtDesc();

    Optional<LockerPeriodJpaEntity> findByIdAndIsDeletedFalse(Long id);

    boolean existsByIdAndIsPublishedTrueAndIsDeletedFalse(Long id);

    Optional<LockerPeriodJpaEntity> findByIdAndIsPublishedTrueAndIsDeletedFalse(Long id);

    List<LockerPeriodJpaEntity> findAllByIdInAndIsPublishedTrueAndIsDeletedFalse(Collection<Long> ids);
}
