package kr.ac.kookmin.stream.db.file;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.file.domain.File;
import kr.ac.kookmin.stream.file.repository.FileRepository;
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
    public List<File> findAllByIdIn(List<Long> ids) {
        return fileJpaRepository.findAllByIdIn(ids).stream().map(FileJpaEntity::toDomain).toList();
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
