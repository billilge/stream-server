package kr.ac.kookmin.stream.event.domain.event.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCreateCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestionCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventSummary;
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
    private final EventWriteValidator eventWriteValidator;

    @Override
    @Transactional(readOnly = true)
    public CursorSliceResult<EventSummary> getPublishedEvents(
        RecruitStatus recruitStatus,
        EventCursor cursor,
        int size
    ) {
        // 필터링(DB)과 응답(도메인)이 같은 시각을 봐야 모집 상태가 어긋나지 않는다
        LocalDateTime now = LocalDateTime.now();
        // 다음 페이지가 있는지 알려면 한 건 더 읽어봐야 한다
        List<Event> fetched = eventRepository.findPublishedSlice(recruitStatus, cursor, size + 1, now);

        // 신청자 수는 한 번에 모아 조회한다. 행사마다 따로 세면 페이지 크기만큼 쿼리가 더 나간다
        Map<Long, Long> applicantCounts = eventRepository.countAppliedByEventIds(
            fetched.stream().limit(size).map(Event::getId).toList());

        return CursorSliceResult.ofSlice(
            fetched,
            size,
            event -> EventSummary.of(event, applicantCounts.getOrDefault(event.getId(), 0L), now),
            summary -> EventCursor.of(summary).format()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public EventDetail getPublishedEvent(Long eventId) {
        Event event = eventRepository.findPublishedById(eventId)
            .orElseThrow(() -> new BusinessException(EventErrorCode.EVENT_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        return EventDetail.of(event, eventRepository.countAppliedByEventId(eventId), now);
    }

    @Override
    @Transactional
    public Event create(Long createdBy, EventCreateCommand command) {
        eventWriteValidator.validate(command);

        Event event = eventRepository.save(Event.create(createdBy, command));
        eventRepository.saveQuestions(toQuestions(event.getId(), command.questions()));
        return event;
    }

    // 신청서에 보이는 순서는 요청 목록의 순서를 따른다
    private static List<EventQuestion> toQuestions(Long eventId, List<EventQuestionCommand> commands) {
        return IntStream.range(0, commands.size())
            .mapToObj(index -> EventQuestion.create(eventId, index + 1, commands.get(index)))
            .toList();
    }
}
