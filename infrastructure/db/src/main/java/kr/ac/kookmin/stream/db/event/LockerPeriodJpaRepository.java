package kr.ac.kookmin.stream.db.event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LockerPeriodJpaRepository extends JpaRepository<LockerPeriodJpaEntity, Long> {

    boolean existsByIdAndIsPublishedTrue(Long id);
}
