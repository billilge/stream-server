package kr.ac.kookmin.stream.event.domain.event.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.OutboxWriter;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.common.event.EventApplicationCanceledByAdmin;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationAnswer;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationManageDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.repository.EventApplicationRepository;
import kr.ac.kookmin.stream.event.domain.event.repository.EventRepository;
import kr.ac.kookmin.stream.event.domain.event.service.AdminEventApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class AdminEventApplicationServiceImpl implements AdminEventApplicationService {

    private final EventRepository eventRepository;
    private final EventApplicationRepository eventApplicationRepository;
    private final OutboxWriter outboxWriter;

    @Override
    public PageResult<EventApplication> getApplications(
        Long eventId,
        EventApplicationStatus status,
        List<Long> memberIds,
        PageOffset pageOffset
    ) {
        return eventApplicationRepository.searchByEventId(eventId, status, memberIds, pageOffset);
    }

    @Override
    public Map<Long, List<EventApplicationAnswer>> getAnswersByApplicationIds(List<Long> applicationIds) {
        return eventApplicationRepository.findAnswersByApplicationIds(applicationIds).stream()
            .collect(Collectors.groupingBy(EventApplicationAnswer::getEventApplicationId));
    }

    @Override
    public long countApplications(Long eventId) {
        return eventApplicationRepository.countByEventId(eventId);
    }

    // 신청은 행사를 지워도 남으므로, 삭제된 행사의 신청이 보이지 않도록 행사를 먼저 확인한다
    @Override
    public EventApplicationManageDetail getApplicationDetail(Long eventId, Long applicationId) {
        Event event = getEventOrThrow(eventId);
        EventApplication application = getApplicationOrThrow(eventId, applicationId);

        return new EventApplicationManageDetail(application, EventApplicationDetail.of(
            event,
            application,
            eventRepository.findQuestionsByEventId(eventId),
            eventApplicationRepository.findAnswersByApplicationId(applicationId)
        ));
    }

    // 학생 본인 취소는 신청 마감까지지만, 마감 뒤 연락해 온 학생도 처리할 수 있도록 운영진은 행사가 끝나기 전까지 취소할 수 있다
    @Override
    @Transactional
    public void cancelApplication(Long eventId, Long applicationId) {
        Event event = getEventOrThrow(eventId);
        EventApplication application = getApplicationOrThrow(eventId, applicationId);

        // 종료 판정과 취소 시각이 같은 시각을 봐야 종료 직전 취소가 종료 이후로 기록되지 않는다
        LocalDateTime now = LocalDateTime.now();
        if (event.isEnded(now)) {
            throw new BusinessException(EventErrorCode.EVENT_ALREADY_ENDED);
        }

        application.cancel(now);
        eventApplicationRepository.save(application);
        outboxWriter.record(new EventApplicationCanceledByAdmin(
            application.getMemberId(), eventId, applicationId, event.getTitle()));
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new BusinessException(EventErrorCode.EVENT_NOT_FOUND));
    }

    private EventApplication getApplicationOrThrow(Long eventId, Long applicationId) {
        return eventApplicationRepository.findByIdAndEventId(applicationId, eventId)
            .orElseThrow(() -> new BusinessException(EventErrorCode.APPLICATION_NOT_FOUND));
    }
}
