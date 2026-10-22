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
     * 운영진용 공지 목록. 종류({@code category})와 제목 검색어({@code keyword})로 거르고 고정 우선, 최신순으로
     * 페이지 단위 조회한다. 종류가 null이면 전체, 검색어가 null이거나 공백이면 거르지 않는다.
     */
    PageResult<Notice> search(NoticeCategory category, String keyword, PageOffset pageOffset);

    Notice getNotice(Long id);
    Notice create(Long createdBy, NoticeCreateCommand command);

    Notice update(Long id, NoticeUpdateCommand command);

    void delete(Long id);
}
