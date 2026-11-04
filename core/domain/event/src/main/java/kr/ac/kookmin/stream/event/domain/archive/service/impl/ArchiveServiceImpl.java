package kr.ac.kookmin.stream.event.domain.archive.service.impl;

import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.PreviewResult;
import kr.ac.kookmin.stream.event.domain.archive.domain.Archive;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveDetail;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveErrorCode;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveSummary;
import kr.ac.kookmin.stream.event.domain.archive.repository.ArchiveRepository;
import kr.ac.kookmin.stream.event.domain.archive.service.ArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class ArchiveServiceImpl implements ArchiveService {

    private final ArchiveRepository archiveRepository;

    @Override
    public List<ArchiveSummary> getArchives(Integer year, String keyword) {
        return archiveRepository.findAllByYearOrderByStartDateDesc(year, keyword).stream()
            .map(ArchiveSummary::from)
            .toList();
    }

    @Override
    public PreviewResult<ArchiveSummary> searchPreview(String keyword, int limit) {
        PreviewResult<Archive> preview = archiveRepository.findSearchPreview(keyword, limit);

        return new PreviewResult<>(
            preview.content().stream().map(ArchiveSummary::from).toList(),
            preview.totalCount()
        );
    }

    @Override
    public List<Integer> getYears() {
        return archiveRepository.findAllYearsDesc();
    }

    @Override
    public ArchiveDetail getArchive(Long archiveId) {
        Archive archive = archiveRepository.findById(archiveId)
            .orElseThrow(() -> new BusinessException(ArchiveErrorCode.ARCHIVE_NOT_FOUND));

        return ArchiveDetail.from(archive);
    }
}
