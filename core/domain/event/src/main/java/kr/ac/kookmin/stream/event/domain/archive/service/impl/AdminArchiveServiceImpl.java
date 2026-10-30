package kr.ac.kookmin.stream.event.domain.archive.service.impl;

import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.archive.domain.Archive;
import kr.ac.kookmin.stream.event.domain.archive.repository.ArchiveRepository;
import kr.ac.kookmin.stream.event.domain.archive.service.AdminArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class AdminArchiveServiceImpl implements AdminArchiveService {

    private final ArchiveRepository archiveRepository;

    @Override
    public PageResult<Archive> getArchives(String keyword, Boolean exposed, PageOffset pageOffset) {
        return archiveRepository.search(keyword, exposed, pageOffset);
    }
}
