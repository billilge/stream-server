package kr.ac.kookmin.stream.db.event;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {

    private final EventJpaRepository eventJpaRepository;
    private final EventQuestionJpaRepository eventQuestionJpaRepository;
    private final EventApplicationJpaRepository eventApplicationJpaRepository;

    @Override
    public Optional<Event> findById(Long id) {
        return eventJpaRepository.findByIdAndIsDeletedFalse(id).map(EventJpaEntity::toDomain);
    }

    @Override
    public List<EventQuestion> findQuestionsByEventId(Long eventId) {
        return eventQuestionJpaRepository.findAllByEventIdOrderByDisplayOrderAsc(eventId).stream()
            .map(EventQuestionJpaEntity::toDomain)
            .toList();
    }

    @Override
    public long countAppliedByEventId(Long eventId) {
        return eventApplicationJpaRepository.countByEventIdAndStatus(eventId, EventApplicationStatus.APPLIED);
    }
}
