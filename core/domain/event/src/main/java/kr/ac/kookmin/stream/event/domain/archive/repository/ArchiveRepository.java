package kr.ac.kookmin.stream.event.domain.archive.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.event.domain.archive.domain.Archive;
import kr.ac.kookmin.stream.event.domain.archive.domain.ArchiveRelatedLink;

public interface ArchiveRepository {

    /**
     * 활동 시작일 내림차순(같으면 식별자 내림차순)으로 아카이빙을 조회한다.
     * <p>
     * 기간이 주어지면 {@code startDate}가 그 범위에 드는 것만 남긴다. 연도 필터는 컬럼에 함수를 씌우지 않도록
     * 서비스가 연도를 반개구간(시작일 이상, 다음 해 1월 1일 미만)으로 바꿔 넘긴다.
     *
     * @param startInclusive 시작 경계(포함). null이면 기간 조건 없이 전체를 조회한다
     * @param endExclusive   종료 경계(제외). {@code startInclusive}가 있을 때만 쓰인다
     */
    List<Archive> findAllOrderByStartDateDesc(LocalDate startInclusive, LocalDate endExclusive);

    /**
     * 아카이빙이 존재하는 연도를 중복 없이 최신순으로 조회한다.
     */
    List<Integer> findAllYearsDesc();

    Optional<Archive> findById(Long id);

    List<ArchiveRelatedLink> findRelatedLinksByArchiveId(Long archiveId);
}
