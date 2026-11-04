package kr.ac.kookmin.stream.welfare.domain.notice.service;

import java.util.Collection;
import java.util.List;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCursor;

/**
 * 공지 조회. 학생 앱이 쓰고, 운영진도 상세 조회에 같은 API를 쓴다. 운영진의 목록 조회·작성·수정·삭제는 {@link AdminNoticeService}가 맡는다.
 */
public interface NoticeService {
    CursorSliceResult<Notice> getNotices(NoticeCategory category, NoticeCursor cursor, int size);

    Notice getById(Long id);

    /**
     * 삭제되지 않은 공지 중 식별자에 해당하는 것. 순서는 보장하지 않는다.
     * <p>
     * 다른 화면이 공지를 곁들여 보여줄 때(홈 배너의 연결 공지) 그 공지가 아직 있는지 한 번에 확인하는 데
     * 쓴다. 없는 식별자는 그냥 빠지므로 {@link #getById(Long)}와 달리 던지지 않는다.
     */
    List<Notice> getByIds(Collection<Long> ids);
}
