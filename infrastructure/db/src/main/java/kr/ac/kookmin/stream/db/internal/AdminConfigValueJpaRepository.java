package kr.ac.kookmin.stream.db.internal;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminConfigValueJpaRepository extends JpaRepository<AdminConfigValueJpaEntity, Long> {
    Optional<AdminConfigValueJpaEntity> findByConfigKey(String configKey);
}
