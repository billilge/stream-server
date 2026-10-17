package kr.ac.kookmin.stream.db.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitType;
import kr.ac.kookmin.stream.event.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository {

    private final EventJpaRepository eventJpaRepository;
    private final EventQuestionJpaRepository eventQuestionJpaRepository;

    @Override
    public Event save(Event event) {
        return eventJpaRepository.save(EventJpaEntity.from(event)).toDomain();
    }

    @Override
    public boolean delete(Long id) {
        return eventJpaRepository.findByIdAndIsDeletedFalse(id)
            .map(entity -> {
                entity.delete();
                return true;
            })
            .orElse(false);
    }

    @Override
    public List<EventQuestion> saveQuestions(List<EventQuestion> questions) {
        List<EventQuestionJpaEntity> entities = questions.stream()
            .map(EventQuestionJpaEntity::from)
            .toList();
        return eventQuestionJpaRepository.saveAll(entities).stream()
            .map(EventQuestionJpaEntity::toDomain)
            .toList();
    }

    @Override
    public List<EventQuestion> replaceQuestions(Long eventId, List<EventQuestion> questions) {
        eventQuestionJpaRepository.deleteAllByEventId(eventId);
        return saveQuestions(questions);
    }

    @Override
    public void touchUpdatedAt(Long eventId) {
        eventJpaRepository.touchUpdatedAt(eventId);
    }

    @Override
    public PageResult<Event> findPage(String keyword, PageOffset pageOffset) {
        Page<EventJpaEntity> result = eventJpaRepository.search(
            keyword, PageRequest.of(pageOffset.page(), pageOffset.size())
        );

        return PageResult.of(
            result.getContent().stream().map(EventJpaEntity::toDomain).toList(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements()
        );
    }

    @Override
    public Optional<Event> findById(Long id) {
        return eventJpaRepository.findByIdAndIsDeletedFalse(id).map(EventJpaEntity::toDomain);
    }

    @Override
    public Optional<Event> findPublishedById(Long id) {
        return eventJpaRepository.findByIdAndIsDeletedFalseAndIsPublishedTrue(id).map(EventJpaEntity::toDomain);
    }

    @Override
    public List<Event> findAllByIds(List<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return List.of();
        }
        return eventJpaRepository.findAllById(eventIds).stream().map(EventJpaEntity::toDomain).toList();
    }

    @Override
    public List<Event> findPublishedSlice(
        RecruitStatus recruitStatus,
        EventCursor cursor,
        int limit,
        LocalDateTime now
    ) {
        return eventJpaRepository.findPublishedSlice(
            recruitStatus == null,
            recruitStatus == RecruitStatus.BEFORE_OPEN,
            recruitStatus == RecruitStatus.OPEN,
            recruitStatus == RecruitStatus.CLOSED,
            RecruitType.FIRST_COME,
            EventApplicationStatus.APPLIED,
            now,
            cursor == null ? null : cursor.eventStartAt(),
            cursor == null ? null : cursor.eventId(),
            Pageable.ofSize(limit)
        ).stream().map(EventJpaEntity::toDomain).toList();
    }

    @Override
    public List<EventQuestion> findQuestionsByEventId(Long eventId) {
        return eventQuestionJpaRepository.findAllByEventIdOrderByDisplayOrderAsc(eventId).stream()
            .map(EventQuestionJpaEntity::toDomain)
            .toList();
    }
}
