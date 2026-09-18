package kr.ac.kookmin.stream.event.domain.archive.service.impl;

import java.time.LocalDate;
import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.archive.domain.Archive;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveDetail;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveErrorCode;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveSummary;
import kr.ac.kookmin.stream.event.domain.archive.repository.ArchiveRepository;
import kr.ac.kookmin.stream.event.domain.archive.service.ArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class ArchiveServiceImpl implements ArchiveService {

    private static final int FIRST_MONTH = 1;
    private static final int FIRST_DAY = 1;

    private final ArchiveRepository archiveRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ArchiveSummary> getArchives(Integer year) {
        return archiveRepository.findAllOrderByStartDateDesc(startOf(year), startOf(nextYearOf(year)))
            .stream()
            .map(ArchiveSummary::from)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getYears() {
        return archiveRepository.findAllYearsDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public ArchiveDetail getArchive(Long archiveId) {
        Archive archive = archiveRepository.findById(archiveId)
            .orElseThrow(() -> new BusinessException(ArchiveErrorCode.ARCHIVE_NOT_FOUND));

        return ArchiveDetail.of(archive, archiveRepository.findRelatedLinksByArchiveId(archiveId));
    }

    /**
     * 연도를 그 해 1월 1일로 바꾼다. 연도 조건을 컬럼에 함수를 씌우지 않는 범위 비교로 넘기기 위함이다.
     */
    private LocalDate startOf(Integer year) {
        return year == null ? null : LocalDate.of(year, FIRST_MONTH, FIRST_DAY);
    }

    private Integer nextYearOf(Integer year) {
        return year == null ? null : year + 1;
    }
}
