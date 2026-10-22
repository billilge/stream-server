package kr.ac.kookmin.stream.welfare.domain.notice.repository;

import java.util.Optional;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCursor;

public interface NoticeRepository {
    CursorSliceResult<Notice> findAll(NoticeCategory category, NoticeCursor cursor, int size);

    Optional<Notice> findById(Long id);

    /**
     * 공지를 저장한다. 수정이라면 읽은 뒤 다른 요청이 같은 내용을 먼저 고쳤을 때
     * {@code NOTICE_UPDATE_CONFLICT}로 실패한다.
     */
    Notice save(Notice notice);

    /**
     * 공지를 소프트 삭제한다. 이미 삭제됐거나 없는 공지, 또는 읽은 직후 다른 요청이 먼저 삭제한 공지면 false를 돌려준다.
     * 변경은 호출 안에서 바로 DB에 보낸다(커밋은 호출한 쪽 트랜잭션이 한다).
     */
    boolean delete(Long id);
}
