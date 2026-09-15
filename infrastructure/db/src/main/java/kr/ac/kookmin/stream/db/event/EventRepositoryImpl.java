package kr.ac.kookmin.stream.db.event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
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
    public Optional<Event> findPublishedById(Long id) {
        return eventJpaRepository.findByIdAndIsDeletedFalseAndIsPublishedTrue(id).map(EventJpaEntity::toDomain);
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
            RecruitStatus.CLOSED,
            RecruitType.FIRST_COME,
            EventApplicationStatus.APPLIED,
            now,
            cursor == null ? null : cursor.eventStartAt(),
            cursor == null ? null : cursor.eventId(),
            Pageable.ofSize(limit)
        ).stream().map(EventJpaEntity::toDomain).toList();
    }

    @Override
    public Map<Long, Long> countAppliedByEventIds(List<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return Map.of();
        }
        return eventJpaRepository.countApplicantsByEventIds(eventIds, EventApplicationStatus.APPLIED).stream()
            .collect(Collectors.toMap(
                EventApplicantCountProjection::eventId,
                EventApplicantCountProjection::applicantCount));
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
