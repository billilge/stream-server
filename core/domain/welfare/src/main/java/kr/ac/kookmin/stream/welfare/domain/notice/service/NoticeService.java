package kr.ac.kookmin.stream.welfare.domain.notice.service;

import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCreateCommand;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCursor;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeUpdateCommand;

public interface NoticeService {
    CursorSliceResult<Notice> getNotices(NoticeCategory category, NoticeCursor cursor, int size);

    /**
     * 운영진용 공지 목록. 제목 검색어({@code keyword})로 거르고 고정 우선, 최신순으로 페이지 단위 조회한다.
     * 검색어가 null이거나 공백이면 거르지 않는다.
     */
    PageResult<Notice> search(String keyword, PageOffset pageOffset);

    Notice getNotice(Long id);
    Notice create(Long createdBy, NoticeCreateCommand command);

    /**
     * 공지를 수정한다. 명령의 수정 시각이 저장된 값과 다르면(그 사이 다른 관리자가 고친 경우)
     * {@code NOTICE_UPDATE_CONFLICT}로 거부한다.
     */
    Notice update(Long id, NoticeUpdateCommand command);

    void delete(Long id);
    void changePinned(Long id, boolean pinned);
}
