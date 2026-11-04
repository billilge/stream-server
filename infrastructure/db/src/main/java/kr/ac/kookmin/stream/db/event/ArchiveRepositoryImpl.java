package kr.ac.kookmin.stream.db.event;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.DateUtil;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.db.common.LikePatterns;
import kr.ac.kookmin.stream.event.domain.archive.domain.Archive;
import kr.ac.kookmin.stream.event.domain.archive.repository.ArchiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArchiveRepositoryImpl implements ArchiveRepository {

    private final ArchiveJpaRepository archiveJpaRepository;

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
    public PageResult<Archive> search(String keyword, Boolean exposed, PageOffset pageOffset) {
        Page<ArchiveJpaEntity> result = archiveJpaRepository.search(
            LikePatterns.escape(keyword),
            exposed,
            PageRequest.of(pageOffset.page(), pageOffset.size())
        );

        return PageResult.of(
            result.getContent().stream().map(ArchiveJpaEntity::toDomain).toList(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements()
        );
    }

    @Override
    public Optional<Archive> findById(Long id) {
        return archiveJpaRepository.findByIdAndIsDeletedFalse(id).map(ArchiveJpaEntity::toDomain);
    }

    @Override
    public List<Archive> findAllByIds(List<Long> archiveIds) {
        if (archiveIds.isEmpty()) {
            return List.of();
        }
        return archiveJpaRepository.findAllByIdInAndIsDeletedFalse(archiveIds).stream()
            .map(ArchiveJpaEntity::toDomain)
            .toList();
    }

    @Override
    public Archive save(Archive archive) {
        return archiveJpaRepository.save(ArchiveJpaEntity.from(archive)).toDomain();
    }

    @Override
    public void saveAll(List<Archive> archives) {
        archiveJpaRepository.saveAll(archives.stream().map(ArchiveJpaEntity::from).toList());
    }

    /**
     * 관리 상태 엔티티를 읽어 표시만 바꾼다. 더티 체킹이 is_deleted 한 컬럼만 UPDATE하므로, 도메인이 들고 있지 않은
     * 삭제 표시를 쓰기 경로로 되돌릴 일이 없다 (NoticeRepositoryImpl.delete와 같은 방식).
     */
    @Override
    public boolean delete(Long id) {
        return archiveJpaRepository.findByIdAndIsDeletedFalse(id)
            .map(entity -> {
                entity.delete();
                return true;
            })
            .orElse(false);
    }
}
