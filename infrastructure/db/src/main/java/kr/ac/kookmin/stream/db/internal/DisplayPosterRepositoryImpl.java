package kr.ac.kookmin.stream.db.internal;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.internal.domain.display.domain.DisplayPoster;
import kr.ac.kookmin.stream.internal.domain.display.repository.DisplayPosterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DisplayPosterRepositoryImpl implements DisplayPosterRepository {

    private final DisplayPosterJpaRepository displayPosterJpaRepository;

    @Override
    public Optional<DisplayPoster> findById(Long id) {
        return displayPosterJpaRepository.findById(id).map(DisplayPosterJpaEntity::toDomain);
    }

    @Override
    public List<DisplayPoster> findAll() {
        return displayPosterJpaRepository.findAllByOrderByIdDesc().stream()
            .map(DisplayPosterJpaEntity::toDomain)
            .toList();
    }

    @Override
    public DisplayPoster save(DisplayPoster poster) {
        return displayPosterJpaRepository.save(DisplayPosterJpaEntity.from(poster)).toDomain();
    }

    @Override
    public void deleteById(Long id) {
        displayPosterJpaRepository.deleteById(id);
    }
}
