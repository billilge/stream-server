package kr.ac.kookmin.stream.file.repository;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.file.domain.File;

public interface FileRepository {
    Optional<File> findById(Long id);
    List<File> findAllByIdIn(List<Long> ids);
    File save(File file);
    void deleteById(Long id);
}
