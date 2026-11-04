package kr.ac.kookmin.stream.event.domain.archive.service;

import java.util.List;
import kr.ac.kookmin.stream.common.PreviewResult;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveDetail;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveSummary;

public interface ArchiveService {

    /**
     * @param year    조회할 활동 연도. null이면 전체를 조회한다
     * @param keyword 제목 검색어. null이거나 공백뿐이면 검색하지 않는다
     */
    List<ArchiveSummary> getArchives(Integer year, String keyword);

    /**
     * 통합 검색의 아카이빙 섹션. 제목에 검색어가 들어 있는 아카이빙을 목록({@link #getArchives})과 같은 순서로
     * 앞에서 {@code limit}건, 그리고 그 전체 수를 돌려준다. 연도로는 거르지 않는다.
     *
     * @param keyword 제목 검색어. null이거나 공백뿐이면 검색하지 않는다
     */
    PreviewResult<ArchiveSummary> searchPreview(String keyword, int limit);

    /**
     * 아카이빙이 존재하는 연도를 최신순으로 조회한다. 연도 필터와 무관하게 항상 전체를 돌려준다.
     */
    List<Integer> getYears();

    ArchiveDetail getArchive(Long archiveId);
}
