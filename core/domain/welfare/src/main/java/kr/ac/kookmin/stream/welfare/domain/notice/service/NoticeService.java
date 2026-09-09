package kr.ac.kookmin.stream.welfare.domain.notice.service;

import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;

public interface NoticeService {
    CursorSliceResult<Notice> getNotices(NoticeCategory category, String cursor, int size);
    Notice getNotice(Long id);
}
