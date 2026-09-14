package kr.ac.kookmin.stream.db.event;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventJpaRepository extends JpaRepository<EventJpaEntity, Long> {

    Optional<EventJpaEntity> findByIdAndIsDeletedFalse(Long id);
}
