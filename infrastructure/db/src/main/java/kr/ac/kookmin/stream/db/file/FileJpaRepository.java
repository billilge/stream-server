package kr.ac.kookmin.stream.db.file;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileJpaRepository extends JpaRepository<FileJpaEntity, Long> {
    Optional<FileJpaEntity> findByFileKey(String fileKey);
    List<FileJpaEntity> findAllByIdIn(List<Long> ids);
}
