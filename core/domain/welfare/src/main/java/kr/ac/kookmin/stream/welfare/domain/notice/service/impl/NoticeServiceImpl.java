package kr.ac.kookmin.stream.welfare.domain.notice.service.impl;

import java.util.Collection;
import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.common.PreviewResult;
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

    // 조회 쿼리가 1개라 트랜잭션을 걸지 않는다. 쿼리가 늘어 한 스냅샷이 필요해지면 @Transactional(readOnly = true)를 붙인다.
    @Override
    public CursorSliceResult<Notice> getNotices(NoticeCategory category, String keyword, NoticeCursor cursor, int size) {
        // 커서가 다른 category 필터에서 발급됐다면 keyset 경계가 다른 정렬 결과를 가리키므로 거부한다
        if (cursor != null && cursor.category() != category) {
            throw new BusinessException(NoticeErrorCode.NOTICE_INVALID_CURSOR);
        }
        return noticeRepository.findAll(category, keyword, cursor, size);
    }

    // 미리보기와 전체 수를 두 문장으로 조회한다. 저장소의 @Query 메서드에는 트랜잭션이 붙지 않으므로, 두 조회가 같은
    // 스냅샷을 보도록 읽기 트랜잭션으로 묶는다
    @Override
    @Transactional(readOnly = true)
    public PreviewResult<Notice> searchPreview(String keyword, int limit) {
        return noticeRepository.findSearchPreview(keyword, limit);
    }

    @Override
    public Notice getById(Long id) {
        return noticeRepository.findById(id)
            .orElseThrow(() -> new BusinessException(NoticeErrorCode.NOTICE_NOT_FOUND));
    }

    @Override
    public List<Notice> getByIds(Collection<Long> ids) {
        return noticeRepository.findAllByIds(ids);
    }
}
