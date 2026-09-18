package kr.ac.kookmin.stream.event.domain.archive.repository;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.event.domain.archive.domain.Archive;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveRelatedLink;

public interface ArchiveRepository {

    /**
     * 활동 시작일 내림차순(같으면 식별자 내림차순)으로 아카이빙을 조회한다.
     *
     * @param year 활동 연도. null이면 연도 조건 없이 전체를 조회한다
     */
    List<Archive> findAllByYearOrderByStartDateDesc(Integer year);

    /**
     * 아카이빙이 존재하는 연도를 중복 없이 최신순으로 조회한다.
     */
    List<Integer> findAllYearsDesc();

    Optional<Archive> findById(Long id);

    List<ArchiveRelatedLink> findRelatedLinksByArchiveId(Long archiveId);
}
