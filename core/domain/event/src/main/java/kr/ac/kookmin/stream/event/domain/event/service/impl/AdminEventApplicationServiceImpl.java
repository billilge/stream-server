package kr.ac.kookmin.stream.event.domain.event.service.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationAnswer;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.repository.EventApplicationRepository;
import kr.ac.kookmin.stream.event.domain.event.repository.EventRepository;
import kr.ac.kookmin.stream.event.domain.event.service.AdminEventApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class AdminEventApplicationServiceImpl implements AdminEventApplicationService {

    private final EventRepository eventRepository;
    private final EventApplicationRepository eventApplicationRepository;

    @Override
    public PageResult<EventApplication> getApplications(
        Long eventId,
        EventApplicationStatus status,
        List<Long> memberIds,
        PageOffset pageOffset
    ) {
        getEventOrThrow(eventId);
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

    @Override
    public EventApplication getApplication(Long eventId, Long applicationId) {
        getEventOrThrow(eventId);
        return eventApplicationRepository.findByIdAndEventId(applicationId, eventId)
            .orElseThrow(() -> new BusinessException(EventErrorCode.APPLICATION_NOT_FOUND));
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() -> new BusinessException(EventErrorCode.EVENT_NOT_FOUND));
    }
}
