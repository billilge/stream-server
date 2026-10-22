package kr.ac.kookmin.stream.db.file;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileJpaRepository extends JpaRepository<FileJpaEntity, Long> {
    List<FileJpaEntity> findAllByIdIn(List<Long> ids);

    // file_key는 TEXT라 idx_files_file_key가 prefix(255) 인덱스다(V20)
    Optional<FileJpaEntity> findByFileKey(String fileKey);
}
