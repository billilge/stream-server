package kr.ac.kookmin.stream.db.event;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.event.domain.archive.domain.Archive;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveRelatedLink;
import kr.ac.kookmin.stream.event.domain.archive.repository.ArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArchiveRepositoryImpl implements ArchiveRepository {

    private final ArchiveJpaRepository archiveJpaRepository;
    private final ArchiveRelatedLinkJpaRepository archiveRelatedLinkJpaRepository;

    @Override
    public List<Archive> findAllOrderByStartDateDesc(LocalDate startInclusive, LocalDate endExclusive) {
        return archiveJpaRepository.findAllInPeriod(startInclusive, endExclusive).stream()
            .map(ArchiveJpaEntity::toDomain)
            .toList();
    }

    @Override
    public List<Integer> findAllYearsDesc() {
        return archiveJpaRepository.findAllYearsDesc();
    }

    @Override
    public Optional<Archive> findById(Long id) {
        return archiveJpaRepository.findById(id).map(ArchiveJpaEntity::toDomain);
    }

    @Override
    public List<ArchiveRelatedLink> findRelatedLinksByArchiveId(Long archiveId) {
        return archiveRelatedLinkJpaRepository.findAllByArchiveIdOrderByDisplayOrderAscIdAsc(archiveId).stream()
            .map(ArchiveRelatedLinkJpaEntity::toDomain)
            .toList();
    }
}
