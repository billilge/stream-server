package kr.ac.kookmin.stream.event.domain.archive.service;

import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveDetail;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveList;

public interface ArchiveService {

    /**
     * @param year 조회할 활동 연도. null이면 전체를 조회한다
     */
    ArchiveList getArchives(Integer year);

    ArchiveDetail getArchive(Long archiveId);
}
