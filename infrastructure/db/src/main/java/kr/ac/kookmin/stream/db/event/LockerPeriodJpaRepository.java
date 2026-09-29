package kr.ac.kookmin.stream.db.event;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LockerPeriodJpaRepository extends JpaRepository<LockerPeriodJpaEntity, Long> {

    boolean existsByIdAndIsPublishedTrue(Long id);

    Optional<LockerPeriodJpaEntity> findByIdAndIsPublishedTrue(Long id);

    List<LockerPeriodJpaEntity> findAllByIdInAndIsPublishedTrue(Collection<Long> ids);
}
