package kr.ac.kookmin.stream.welfare.domain.notice.repository;

import java.util.Optional;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCursor;

public interface NoticeRepository {
    CursorSliceResult<Notice> findAll(NoticeCategory category, NoticeCursor cursor, int size);
    Optional<Notice> findById(Long id);
    Notice save(Notice notice);

    /**
     * 공지를 소프트 삭제한다. 이미 삭제됐거나 없는 공지면 false를 돌려준다.
     * 변경은 호출한 쪽 트랜잭션이 커밋될 때 반영된다.
     */
    boolean delete(Long id);
}
