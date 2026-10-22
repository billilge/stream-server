package kr.ac.kookmin.stream.welfare.domain.notice.repository;

import java.util.Optional;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCursor;

public interface NoticeRepository {
    CursorSliceResult<Notice> findAll(NoticeCategory category, NoticeCursor cursor, int size);

    /**
     * 삭제되지 않은 공지를 고정 우선, 최신순으로 페이지 단위로 조회한다. 제목에 {@code keyword}가 들어 있는 공지만 고르고
     * {@code keyword}가 null이면 조건 없이 모두 조회한다.
     */
    PageResult<Notice> search(String keyword, PageOffset pageOffset);

    Optional<Notice> findById(Long id);
    Notice save(Notice notice);

    /**
     * 공지를 소프트 삭제한다. 이미 삭제됐거나 없는 공지면 false를 돌려준다.
     * 변경은 호출한 쪽 트랜잭션이 커밋될 때 반영된다.
     */
    boolean delete(Long id);
}
