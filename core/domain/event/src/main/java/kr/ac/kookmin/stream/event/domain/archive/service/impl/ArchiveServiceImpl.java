package kr.ac.kookmin.stream.event.domain.archive.service.impl;

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

    private final ArchiveRepository archiveRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ArchiveSummary> getArchives(Integer year) {
        return archiveRepository.findAllByYearOrderByStartDateDesc(year).stream()
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
}
