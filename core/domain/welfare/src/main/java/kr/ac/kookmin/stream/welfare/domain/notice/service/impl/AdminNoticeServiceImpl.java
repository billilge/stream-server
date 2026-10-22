package kr.ac.kookmin.stream.welfare.domain.notice.service.impl;

import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.OutboxWriter;
import kr.ac.kookmin.stream.common.event.NoticeCreated;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.Notice;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeCreateCommand;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeErrorCode;
import kr.ac.kookmin.stream.welfare.domain.notice.domain.NoticeUpdateCommand;
import kr.ac.kookmin.stream.welfare.domain.notice.repository.NoticeRepository;
import kr.ac.kookmin.stream.welfare.domain.notice.service.AdminNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class AdminNoticeServiceImpl implements AdminNoticeService {

    private final NoticeRepository noticeRepository;
    private final OutboxWriter outboxWriter;

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
        Notice notice = noticeRepository.findById(id)
            .orElseThrow(() -> new BusinessException(NoticeErrorCode.NOTICE_NOT_FOUND));
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
