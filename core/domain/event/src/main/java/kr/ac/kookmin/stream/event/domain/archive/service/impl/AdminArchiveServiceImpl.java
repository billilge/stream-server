package kr.ac.kookmin.stream.event.domain.archive.service.impl;

import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.archive.domain.Archive;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveWriteCommand;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveErrorCode;
import kr.ac.kookmin.stream.event.domain.archive.repository.ArchiveRepository;
import kr.ac.kookmin.stream.event.domain.archive.service.AdminArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class AdminArchiveServiceImpl implements AdminArchiveService {

    private final ArchiveRepository archiveRepository;

    @Override
    public PageResult<Archive> getArchives(String keyword, Boolean exposed, PageOffset pageOffset) {
        return archiveRepository.search(keyword, exposed, pageOffset);
    }

    @Override
    @Transactional
    public void create(ArchiveWriteCommand command) {
        archiveRepository.save(Archive.create(command));
    }

    @Override
    @Transactional
    public void update(Long archiveId, ArchiveWriteCommand command) {
        Archive archive = getArchive(archiveId);
        archive.update(command);

        archiveRepository.save(archive);
    }

    @Override
    @Transactional
    public void delete(Long archiveId) {
        if (!archiveRepository.delete(archiveId)) {
            throw new BusinessException(ArchiveErrorCode.ARCHIVE_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public void changeExposure(List<Long> archiveIds, boolean exposed) {
        // 같은 식별자가 두 번 오면 아래 개수 비교가 어긋나므로, 호출부에 맡기지 않고 여기서 걷어낸다
        List<Long> distinctIds = archiveIds.stream().distinct().toList();
        List<Archive> archives = archiveRepository.findAllByIds(distinctIds);
        // 하나라도 없으면 아무것도 바꾸지 않는다. 삭제된 아카이빙은 findAllByIds가 빼므로 수가 모자란다
        if (archives.size() != distinctIds.size()) {
            throw new BusinessException(ArchiveErrorCode.ARCHIVE_NOT_FOUND);
        }

        archives.forEach(archive -> archive.changeExposure(exposed));
        archiveRepository.saveAll(archives);
    }

    private Archive getArchive(Long archiveId) {
        return archiveRepository.findById(archiveId)
            .orElseThrow(() -> new BusinessException(ArchiveErrorCode.ARCHIVE_NOT_FOUND));
    }
}
