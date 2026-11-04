package kr.ac.kookmin.stream.api.app.event.event.usecase;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.EventSummary;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;
import kr.ac.kookmin.stream.event.domain.event.service.EventService;

/**
 * 행사 UseCase 테스트용 행사 서비스. 행사 한 건 조회만 흉내 내고, 이 테스트들이 쓰지 않는 목록 조회는 막아 둔다.
 */
class FakeEventService implements EventService {

    final Map<Long, Event> events = new HashMap<>();

    @Override
    public CursorSliceResult<EventSummary> getPublishedEvents(RecruitStatus recruitStatus, EventCursor cursor, int size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Event> getApplicableEvents(Collection<Long> eventIds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Event> getPublishedByIds(Collection<Long> eventIds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EventDetail getPublishedEvent(Long eventId) {
        Event event = events.get(eventId);
        if (event == null || !event.isPublished()) {
            throw new BusinessException(EventErrorCode.EVENT_NOT_FOUND);
        }
        return EventDetail.of(event, 0, LocalDateTime.now());
    }

    @Override
    public Event getByIdOrNull(Long eventId) {
        return events.get(eventId);
    }
}
