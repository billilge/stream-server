package kr.ac.kookmin.stream.event.domain.archive.service;

import java.util.List;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveDetail;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveSummary;

public interface ArchiveService {

    /**
     * @param year 조회할 활동 연도. null이면 전체를 조회한다
     */
    List<ArchiveSummary> getArchives(Integer year);

    /**
     * 아카이빙이 존재하는 연도를 최신순으로 조회한다. 연도 필터와 무관하게 항상 전체를 돌려준다.
     */
    List<Integer> getYears();

    ArchiveDetail getArchive(Long archiveId);
}
