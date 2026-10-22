package kr.ac.kookmin.stream.welfare.domain.notice.service;

import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCursor;

/**
 * 공지 조회. 학생 앱이 쓰고, 운영진도 목록·상세 조회에 같은 API를 쓴다. 운영진의 작성·수정·삭제는 {@link AdminNoticeService}가 맡는다.
 */
public interface NoticeService {
    /**
     * 공지 목록. 고정 우선, 최신순으로 커서 페이지 조회한다.
     *
     * @param keyword 제목 검색어. null이거나 공백이면 검색하지 않는다
     */
    CursorSliceResult<Notice> getNotices(NoticeCategory category, String keyword, NoticeCursor cursor, int size);

    Notice getNotice(Long id);
}
