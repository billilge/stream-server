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

    Notice getNotice(Long id);
}
