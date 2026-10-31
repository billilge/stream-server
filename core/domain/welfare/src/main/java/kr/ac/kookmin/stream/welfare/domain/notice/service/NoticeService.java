package kr.ac.kookmin.stream.welfare.domain.notice.service;

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
     * 공지 한 건. 없거나 삭제됐으면 {@code null}이다.
     * <p>
     * 다른 화면이 공지를 곁들여 보여줄 때(홈 배너의 연결 공지) 쓴다. 공지가 사라졌다고 그 화면까지 404가 되면
     * 안 되므로 {@link #getById(Long)}와 달리 던지지 않는다.
     */
    Notice getByIdOrNull(Long id);
}
