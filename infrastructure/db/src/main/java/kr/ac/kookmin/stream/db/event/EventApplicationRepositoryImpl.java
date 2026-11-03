package kr.ac.kookmin.stream.db.event;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationAnswer;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import kr.ac.kookmin.stream.event.domain.event.repository.EventApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventApplicationRepositoryImpl implements EventApplicationRepository {

    private final EventApplicationJpaRepository eventApplicationJpaRepository;
    private final EventApplicationAnswerJpaRepository eventApplicationAnswerJpaRepository;

    @Override
    public EventApplication save(EventApplication application) {
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

    @Override
    public Map<Long, Long> countAppliedByEventIds(List<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return Map.of();
        }
        return eventApplicationJpaRepository.countApplicantsByEventIds(eventIds, EventApplicationStatus.APPLIED)
            .stream()
            .collect(Collectors.toMap(
                EventApplicantCountProjection::eventId,
                EventApplicantCountProjection::applicantCount));
    }

    @Override
    public long countAppliedByEventId(Long eventId) {
        return eventApplicationJpaRepository.countByEventIdAndStatus(eventId, EventApplicationStatus.APPLIED);
    }

    @Override
    public long countByEventId(Long eventId) {
        return eventApplicationJpaRepository.countByEventId(eventId);
    }

    @Override
    public boolean existsByEventId(Long eventId) {
        return eventApplicationJpaRepository.existsByEventId(eventId);
    }

    @Override
    public boolean existsAppliedByEventIdAndMemberId(Long eventId, Long memberId) {
        return eventApplicationJpaRepository.existsByEventIdAndMemberIdAndStatus(
            eventId, memberId, EventApplicationStatus.APPLIED
        );
    }

    @Override
    public List<Long> findAppliedMemberIds(Long eventId) {
        return eventApplicationJpaRepository.findMemberIdsByEventIdAndStatus(eventId, EventApplicationStatus.APPLIED);
    }

    @Override
    public List<EventApplication> findSliceByMemberId(Long memberId, EventApplicationCursor cursor, int limit) {
        return eventApplicationJpaRepository.findSliceByMemberId(
            memberId,
            cursor == null ? null : cursor.appliedAt(),
            cursor == null ? null : cursor.applicationId(),
            Pageable.ofSize(limit)
        ).stream().map(EventApplicationJpaEntity::toDomain).toList();
    }

    @Override
    public PageResult<EventApplication> searchByEventId(
        Long eventId,
        EventApplicationStatus status,
        List<Long> memberIds,
        PageOffset pageOffset
    ) {
        boolean filterByMember = memberIds != null;
        Page<EventApplicationJpaEntity> result = eventApplicationJpaRepository.searchByEventId(
            eventId, status, filterByMember, filterByMember ? memberIds : List.of(),
            PageRequest.of(pageOffset.page(), pageOffset.size())
        );

        return PageResult.of(
            result.getContent().stream().map(EventApplicationJpaEntity::toDomain).toList(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements()
        );
    }

    @Override
    public Optional<EventApplication> findByIdAndMemberId(Long applicationId, Long memberId) {
        return eventApplicationJpaRepository.findByIdAndMemberId(applicationId, memberId)
            .map(EventApplicationJpaEntity::toDomain);
    }

    @Override
    public Optional<EventApplication> findByIdAndEventId(Long applicationId, Long eventId) {
        return eventApplicationJpaRepository.findByIdAndEventId(applicationId, eventId)
            .map(EventApplicationJpaEntity::toDomain);
    }

    @Override
    public List<EventApplicationAnswer> findAnswersByApplicationId(Long applicationId) {
        return eventApplicationAnswerJpaRepository.findAllByEventApplicationId(applicationId).stream()
            .map(EventApplicationAnswerJpaEntity::toDomain)
            .toList();
    }

    @Override
    public List<EventApplicationAnswer> findAnswersByApplicationIds(List<Long> applicationIds) {
        if (applicationIds.isEmpty()) {
            return List.of();
        }
        return eventApplicationAnswerJpaRepository.findAllByEventApplicationIdIn(applicationIds).stream()
            .map(EventApplicationAnswerJpaEntity::toDomain)
            .toList();
    }
}
