package kr.ac.kookmin.stream.event.domain.event.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationAnswer;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import kr.ac.kookmin.stream.event.domain.event.repository.EventApplicationRepository;

/**
 * 운영진 행사 관리 테스트용 신청 메모리 레포지토리. 운영진 기능이 쓰는 메서드만 흉내 내고,
 * 학생 신청·내 신청 내역 쪽 메서드는 이 테스트들이 건드리지 않으므로 막아 둔다.
 */
class InMemoryEventApplicationRepository implements EventApplicationRepository {

    final Map<Long, Long> appliedCounts = new HashMap<>();
    final Map<Long, EventApplication> applications = new HashMap<>();
    final List<EventApplicationAnswer> answers = new ArrayList<>();
    /** 질문 잠금 판정용. 신청 데이터를 넣지 않고도 "신청 있음"을 만들 수 있게 따로 둔다 */
    boolean applicationExists;

    @Override
    public boolean existsByEventId(Long eventId) {
        return applicationExists;
    }

    @Override
    public long countAppliedByEventId(Long eventId) {
        return appliedCounts.getOrDefault(eventId, 0L);
    }

    @Override
    public Map<Long, Long> countAppliedByEventIds(List<Long> eventIds) {
        Map<Long, Long> counts = new HashMap<>();
        eventIds.stream().filter(appliedCounts::containsKey).forEach(id -> counts.put(id, appliedCounts.get(id)));
        return counts;
    }

    @Override
    public PageResult<EventApplication> findPageByEventId(
        Long eventId,
        EventApplicationStatus status,
        List<Long> memberIds,
        PageOffset pageOffset
    ) {
        List<EventApplication> matched = applications.values().stream()
            .filter(application -> application.getEventId().equals(eventId))
            .filter(application -> status == null || application.getStatus() == status)
            .filter(application -> memberIds == null || memberIds.contains(application.getMemberId()))
            .sorted(Comparator.comparing(EventApplication::getAppliedAt).thenComparing(EventApplication::getId))
            .toList();
        List<EventApplication> content = matched.stream()
            .skip((long) pageOffset.page() * pageOffset.size())
            .limit(pageOffset.size())
            .toList();
        return PageResult.of(content, pageOffset.page(), pageOffset.size(), matched.size());
    }

    @Override
    public List<EventApplicationAnswer> findAnswersByApplicationIds(List<Long> applicationIds) {
        return answers.stream()
            .filter(answer -> applicationIds.contains(answer.getEventApplicationId()))
            .toList();
    }

    @Override
    public Optional<EventApplication> findByIdAndEventId(Long applicationId, Long eventId) {
        return Optional.ofNullable(applications.get(applicationId))
            .filter(application -> application.getEventId().equals(eventId));
    }

    @Override
    public long countByEventId(Long eventId) {
        return applications.values().stream().filter(application -> application.getEventId().equals(eventId)).count();
    }

    @Override
    public boolean existsAppliedByEventIdAndMemberId(Long eventId, Long memberId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EventApplication save(EventApplication application) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<EventApplicationAnswer> saveAnswers(List<EventApplicationAnswer> saved) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<EventApplication> findSliceByMemberId(Long memberId, EventApplicationCursor cursor, int limit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<EventApplication> findByIdAndMemberId(Long applicationId, Long memberId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<EventApplicationAnswer> findAnswersByApplicationId(Long applicationId) {
        throw new UnsupportedOperationException();
    }
}
