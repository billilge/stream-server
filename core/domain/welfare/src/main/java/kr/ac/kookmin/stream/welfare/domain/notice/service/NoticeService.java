package kr.ac.kookmin.stream.welfare.domain.notice.service;

import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCreateCommand;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCursor;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeUpdateCommand;

public interface NoticeService {
    CursorSliceResult<Notice> getNotices(NoticeCategory category, NoticeCursor cursor, int size);
    Notice getNotice(Long id);
    Notice create(NoticeCreateCommand command);

    /**
     * 공지를 수정한다. 명령의 수정 시각이 저장된 값과 다르면(그 사이 다른 관리자가 고친 경우)
     * {@code NOTICE_UPDATE_CONFLICT}로 거부한다.
     */
    Notice update(Long id, NoticeUpdateCommand command);

    void delete(Long id);
    void changePinned(Long id, boolean pinned);
}
