package kr.ac.kookmin.stream.db.event;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LockerSectionLayoutJpaRepository extends JpaRepository<LockerSectionLayoutJpaEntity, Long> {

    Optional<LockerSectionLayoutJpaEntity> findBySectionId(Long sectionId);
}
