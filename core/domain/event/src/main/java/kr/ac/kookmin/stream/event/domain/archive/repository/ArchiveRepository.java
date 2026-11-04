package kr.ac.kookmin.stream.event.domain.archive.repository;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.archive.domain.Archive;

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

    /**
     * 운영진 목록용. 등록일 최신순(같으면 식별자 내림차순)으로 페이징한다. 학생 앱 목록이 활동 시작일 기준인 것과 다르다.
     *
     * @param keyword           제목 일부. null이면 제목으로 좁히지 않는다
     * @param exposed 슬랑제 노출 여부. null이면 노출 여부로 좁히지 않는다
     */
    PageResult<Archive> search(String keyword, Boolean exposed, PageOffset pageOffset);

    Optional<Archive> findById(Long id);

    /**
     * 식별자 목록에 드는 아카이빙. 삭제된 것은 담지 않는다. 노출 여부 일괄 변경이 요청한 전부가 살아 있는지
     * 확인하려고 쓰므로, 결과 수가 요청 수와 다르면 부르는 쪽이 끊는다.
     */
    List<Archive> findAllByIds(List<Long> archiveIds);

    Archive save(Archive archive);

    void saveAll(List<Archive> archives);

    /**
     * 소프트 삭제한다. 이미 없거나 지워진 아카이빙이면 {@code false}다.
     */
    boolean delete(Long id);
}
