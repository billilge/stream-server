package kr.ac.kookmin.stream.welfare.domain.notice.repository;

import java.util.Optional;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;

public interface NoticeRepository {
    CursorSliceResult<Notice> findAll(NoticeCategory category, String cursor, int size);
    Optional<Notice> findById(Long id);
}
