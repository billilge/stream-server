package kr.ac.kookmin.stream.db.file;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileJpaRepository extends JpaRepository<FileJpaEntity, Long> {
    List<FileJpaEntity> findAllByIdIn(List<Long> ids);
}
