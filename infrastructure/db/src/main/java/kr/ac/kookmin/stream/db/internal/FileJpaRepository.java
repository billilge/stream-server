package kr.ac.kookmin.stream.db.internal;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileJpaRepository extends JpaRepository<FileJpaEntity, Long> {
    Optional<FileJpaEntity> findByFileKey(String fileKey);
}
