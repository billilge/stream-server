package kr.ac.kookmin.stream.event.domain.event.service.impl;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationForm;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;
import kr.ac.kookmin.stream.event.domain.event.repository.EventRepository;
import kr.ac.kookmin.stream.event.domain.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    @Transactional(readOnly = true)
    public EventApplicationForm getApplicationForm(Long eventId) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new BusinessException(EventErrorCode.EVENT_NOT_FOUND));

        long appliedCount = eventRepository.countAppliedByEventId(eventId);
        if (event.calculateRecruitStatus(LocalDateTime.now(), appliedCount) != RecruitStatus.OPEN) {
            throw new BusinessException(EventErrorCode.ALREADY_CLOSED);
        }

        return EventApplicationForm.of(event, eventRepository.findQuestionsByEventId(eventId));
    }
}
