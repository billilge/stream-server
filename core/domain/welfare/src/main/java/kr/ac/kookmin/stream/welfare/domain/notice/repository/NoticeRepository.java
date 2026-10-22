package kr.ac.kookmin.stream.welfare.domain.notice.repository;

import java.util.Optional;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCursor;

public interface NoticeRepository {
    /**
     * 삭제되지 않은 공지를 고정 우선, 최신순으로 커서 페이지 조회한다.
     *
     * @param category 카테고리. null이면 전체
     * @param keyword  제목에 포함된 검색어. null이면 검색하지 않는다
     * @param cursor   이전 페이지의 마지막 항목. null이면 첫 페이지
     */
    CursorSliceResult<Notice> findAll(NoticeCategory category, String keyword, NoticeCursor cursor, int size);

    Optional<Notice> findById(Long id);
    Notice save(Notice notice);

    /**
     * 공지를 소프트 삭제한다. 이미 삭제됐거나 없는 공지면 false를 돌려준다.
     * 변경은 호출한 쪽 트랜잭션이 커밋될 때 반영된다.
     */
    boolean delete(Long id);
}
