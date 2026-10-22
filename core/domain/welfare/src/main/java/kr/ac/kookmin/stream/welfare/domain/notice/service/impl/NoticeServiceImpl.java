package kr.ac.kookmin.stream.welfare.domain.notice.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    @Override
    @Transactional(readOnly = true)
    public CursorSliceResult<Notice> getNotices(NoticeCategory category, NoticeCursor cursor, int size) {
        // 커서가 다른 category 필터에서 발급됐다면 keyset 경계가 다른 정렬 결과를 가리키므로 거부한다
        if (cursor != null && cursor.category() != category) {
            throw new BusinessException(NoticeErrorCode.NOTICE_INVALID_CURSOR);
        }
        return noticeRepository.findAll(category, cursor, size);
    }

    // 목록과 전체 개수를 두 번 조회하므로 같은 스냅샷에서 읽도록 읽기 트랜잭션으로 묶는다
    @Override
    @Transactional(readOnly = true)
    public PageResult<Notice> search(String keyword, PageOffset pageOffset) {
        String normalizedKeyword = keyword == null || keyword.isBlank() ? null : keyword.trim();
        return noticeRepository.search(normalizedKeyword, pageOffset);
    }

    @Override
    @Transactional(readOnly = true)
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
        validateNotModified(notice, command.expectedUpdatedAt());

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

    // updated_at이 초 단위 DATETIME이라 같은 초로 맞춰 비교한다. 수정 화면이 읽은 뒤 고쳐졌다면 시각이 달라진다
    private void validateNotModified(Notice notice, LocalDateTime expectedUpdatedAt) {
        LocalDateTime current = notice.getUpdatedAt().truncatedTo(ChronoUnit.SECONDS);
        if (!current.equals(expectedUpdatedAt.truncatedTo(ChronoUnit.SECONDS))) {
            throw new BusinessException(NoticeErrorCode.NOTICE_UPDATE_CONFLICT);
        }
    }
}
