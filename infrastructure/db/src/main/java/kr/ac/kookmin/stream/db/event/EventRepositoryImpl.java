package kr.ac.kookmin.stream.db.event;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationAnswer;
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
    private final EventApplicationAnswerJpaRepository eventApplicationAnswerJpaRepository;

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

    @Override
    public boolean existsAppliedByEventIdAndMemberId(Long eventId, Long memberId) {
        return eventApplicationJpaRepository.existsByEventIdAndMemberIdAndStatus(
            eventId, memberId, EventApplicationStatus.APPLIED
        );
    }

    @Override
    public EventApplication saveApplication(EventApplication application) {
        return eventApplicationJpaRepository.save(EventApplicationJpaEntity.from(application)).toDomain();
    }

    @Override
    public List<EventApplicationAnswer> saveAnswers(List<EventApplicationAnswer> answers) {
        List<EventApplicationAnswerJpaEntity> entities = answers.stream()
            .map(EventApplicationAnswerJpaEntity::from)
            .toList();
        return eventApplicationAnswerJpaRepository.saveAll(entities).stream()
            .map(EventApplicationAnswerJpaEntity::toDomain)
            .toList();
    }
}
