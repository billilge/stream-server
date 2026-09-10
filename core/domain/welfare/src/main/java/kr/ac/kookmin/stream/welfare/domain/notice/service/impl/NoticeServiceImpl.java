package kr.ac.kookmin.stream.welfare.domain.notice.service.impl;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCursor;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeErrorCode;
import kr.ac.kookmin.stream.welfare.domain.notice.repository.NoticeRepository;
import kr.ac.kookmin.stream.welfare.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    @Transactional(readOnly = true)
    public CursorSliceResult<Notice> getNotices(NoticeCategory category, NoticeCursor cursor, int size) {
        // 커서가 다른 category 필터에서 발급됐다면 keyset 경계가 다른 정렬 결과를 가리키므로 거부한다
        if (cursor != null && cursor.category() != category) {
            throw new BusinessException(NoticeErrorCode.NOTICE_INVALID_CURSOR);
        }
        return noticeRepository.findAll(category, cursor, size);
    }

    @Override
    @Transactional(readOnly = true)
    public Notice getNotice(Long id) {
        return noticeRepository.findById(id)
            .orElseThrow(() -> new BusinessException(NoticeErrorCode.NOTICE_NOT_FOUND));
    }
}
