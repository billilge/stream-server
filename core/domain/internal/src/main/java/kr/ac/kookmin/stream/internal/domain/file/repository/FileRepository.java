package kr.ac.kookmin.stream.internal.domain.file.repository;

import java.util.Optional;
import kr.ac.kookmin.stream.internal.domain.file.domain.File;

public interface FileRepository {
    Optional<File> findById(Long id);
    Optional<File> findByFileKey(String fileKey);
    File save(File file);
    void deleteById(Long id);
}
