package kr.ac.kookmin.stream.db.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicantCount;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationAnswer;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitType;
import kr.ac.kookmin.stream.event.domain.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
    public CursorSliceResult<EventApplicantCount> findPublishedSlice(
        RecruitStatus recruitStatus,
        EventCursor cursor,
        int size,
        LocalDateTime now
    ) {
        // 다음 페이지 존재 여부를 알기 위해 한 건 더 읽는다
        Pageable pageable = Pageable.ofSize(size + 1);
        List<EventJpaEntity> entities = eventJpaRepository.findPublishedSlice(
            recruitStatus == null,
            recruitStatus == RecruitStatus.BEFORE_OPEN,
            recruitStatus == RecruitStatus.OPEN,
            recruitStatus == RecruitStatus.CLOSED,
            RecruitStatus.CLOSED,
            RecruitType.FIRST_COME,
            EventApplicationStatus.APPLIED,
            now,
            cursor == null ? null : cursor.eventStartAt(),
            cursor == null ? null : cursor.eventId(),
            pageable
        );

        boolean hasNext = entities.size() > size;
        List<Event> events = entities.stream()
            .limit(size)
            .map(EventJpaEntity::toDomain)
            .toList();

        Map<Long, Long> applicantCounts = countApplicants(events);
        List<EventApplicantCount> content = events.stream()
            .map(event -> new EventApplicantCount(event, applicantCounts.getOrDefault(event.getId(), 0L)))
            .toList();

        String nextCursor = hasNext ? EventCursor.of(events.getLast()).format() : null;

        return new CursorSliceResult<>(content, hasNext, nextCursor);
    }

    private Map<Long, Long> countApplicants(List<Event> events) {
        if (events.isEmpty()) {
            return Map.of();
        }
        List<Long> eventIds = events.stream().map(Event::getId).toList();
        return eventJpaRepository.countApplicantsByEventIds(eventIds, EventApplicationStatus.APPLIED).stream()
            .collect(Collectors.toMap(EventApplicantCountRow::eventId, EventApplicantCountRow::applicantCount));
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
