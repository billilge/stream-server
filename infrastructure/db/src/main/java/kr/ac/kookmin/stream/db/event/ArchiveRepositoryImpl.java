package kr.ac.kookmin.stream.db.event;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.DateUtil;
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

    /**
     * 연도를 반개구간으로 바꿔 조회한다. {@code YEAR(start_date) = ?}처럼 컬럼에 함수를 씌우면 인덱스를
     * 쓸 수 없어서인데, 어떻게 질의할지는 인프라의 몫이므로 변환을 여기서 한다.
     */
    @Override
    public List<Archive> findAllByYearOrderByStartDateDesc(Integer year) {
        return archiveJpaRepository
            .findAllInPeriod(DateUtil.startOfYear(year), DateUtil.startOfNextYear(year)).stream()
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
