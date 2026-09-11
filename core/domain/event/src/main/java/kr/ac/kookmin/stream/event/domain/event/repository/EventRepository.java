package kr.ac.kookmin.stream.event.domain.event.repository;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;

public interface EventRepository {

    Optional<Event> findById(Long id);

    List<EventQuestion> findQuestionsByEventId(Long eventId);

    long countAppliedByEventId(Long eventId);
}
