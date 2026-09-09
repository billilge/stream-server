package kr.ac.kookmin.stream.db.internal;

import java.util.Optional;
import kr.ac.kookmin.stream.internal.domain.file.domain.File;
import kr.ac.kookmin.stream.internal.domain.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FileRepositoryImpl implements FileRepository {

    private final FileJpaRepository fileJpaRepository;

    @Override
    public Optional<File> findById(Long id) {
        return fileJpaRepository.findById(id).map(FileJpaEntity::toDomain);
    }

    @Override
    public Optional<File> findByFileKey(String fileKey) {
        return fileJpaRepository.findByFileKey(fileKey).map(FileJpaEntity::toDomain);
    }

    @Override
    public File save(File file) {
        return fileJpaRepository.save(FileJpaEntity.from(file)).toDomain();
    }

    @Override
    public void deleteById(Long id) {
        fileJpaRepository.deleteById(id);
    }
}
