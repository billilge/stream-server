package kr.ac.kookmin.stream.welfare.domain.notice.service.impl;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.common.OutboxWriter;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.common.event.NoticeCreated;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCategory;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCreateCommand;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCursor;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeErrorCode;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeUpdateCommand;
import kr.ac.kookmin.stream.welfare.domain.notice.repository.NoticeRepository;
import kr.ac.kookmin.stream.welfare.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final OutboxWriter outboxWriter;

    // 조회 쿼리가 1개라 트랜잭션을 걸지 않는다. 쿼리가 늘어 한 스냅샷이 필요해지면 @Transactional(readOnly = true)를 붙인다.
    @Override
    public CursorSliceResult<Notice> getNotices(NoticeCategory category, NoticeCursor cursor, int size) {
        // 커서가 다른 category 필터에서 발급됐다면 keyset 경계가 다른 정렬 결과를 가리키므로 거부한다
        if (cursor != null && cursor.category() != category) {
            throw new BusinessException(NoticeErrorCode.NOTICE_INVALID_CURSOR);
        }
        return noticeRepository.findAll(category, cursor, size);
    }

    // 목록과 전체 개수를 두 문장으로 조회한다. 저장소의 @Query 메서드에는 트랜잭션이 붙지 않으므로, 두 조회가 같은 스냅샷을
    // 보도록 읽기 트랜잭션으로 묶는다
    @Override
    @Transactional(readOnly = true)
    public PageResult<Notice> search(String keyword, PageOffset pageOffset) {
        String normalizedKeyword = keyword == null || keyword.isBlank() ? null : keyword.strip();
        return noticeRepository.search(normalizedKeyword, pageOffset);
    }

    @Override
    public Notice getNotice(Long id) {
        return noticeRepository.findById(id)
            .orElseThrow(() -> new BusinessException(NoticeErrorCode.NOTICE_NOT_FOUND));
    }

    // 공지 저장과 알림 이벤트 기록이 함께 커밋되도록 트랜잭션을 건다
    @Override
    @Transactional
    public Notice create(Long createdBy, NoticeCreateCommand command) {
        Notice notice = noticeRepository.save(Notice.create(createdBy, command));
        outboxWriter.record(new NoticeCreated(notice.getId()));
        return notice;
    }

    @Override
    @Transactional
    public Notice update(Long id, NoticeUpdateCommand command) {
        Notice notice = getNotice(id);
        notice.update(command);
        return noticeRepository.save(notice);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!noticeRepository.delete(id)) {
            throw new BusinessException(NoticeErrorCode.NOTICE_NOT_FOUND);
        }
    }
}
