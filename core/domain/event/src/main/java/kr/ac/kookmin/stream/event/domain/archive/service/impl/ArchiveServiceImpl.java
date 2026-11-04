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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class ArchiveServiceImpl implements ArchiveService {

    private final ArchiveRepository archiveRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ArchiveSummary> getArchives(Integer year, String keyword) {
        return archiveRepository.findAllByYearOrderByStartDateDesc(year, keyword).stream()
            .map(ArchiveSummary::from)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PreviewResult<ArchiveSummary> searchPreview(String keyword, int limit) {
        // 아카이빙은 연간 수십 건이라 학생 목록도 전체를 한 번에 준다. 미리보기도 같은 조회로 전부 읽고 앞에서 자른다
        List<Archive> archives = archiveRepository.findAllByYearOrderByStartDateDesc(null, keyword);

        return new PreviewResult<>(
            archives.stream().limit(limit).map(ArchiveSummary::from).toList(),
            archives.size()
        );
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

        return ArchiveDetail.from(archive);
    }
}
