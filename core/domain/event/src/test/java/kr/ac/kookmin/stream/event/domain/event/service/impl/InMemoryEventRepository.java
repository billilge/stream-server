package kr.ac.kookmin.stream.event.domain.event.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationAnswer;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;
import kr.ac.kookmin.stream.event.domain.event.repository.EventRepository;

/**
 * 운영진 행사 관리 테스트용 메모리 레포지토리. 운영진 기능이 쓰는 메서드만 흉내 내고,
 * 학생 신청·목록 쪽 메서드는 이 테스트들이 건드리지 않으므로 막아 둔다.
 */
class InMemoryEventRepository implements EventRepository {

    final Map<Long, Event> events = new HashMap<>();
    final Map<Long, List<EventQuestion>> questions = new HashMap<>();
    final Map<Long, Long> appliedCounts = new HashMap<>();
    final Map<Long, EventApplication> applications = new HashMap<>();
    final List<EventApplicationAnswer> answers = new ArrayList<>();
    /** 질문 잠금 판정용. 신청 데이터를 넣지 않고도 "신청 있음"을 만들 수 있게 따로 둔다 */
    boolean applicationExists;
    boolean questionsReplaced;
    boolean updatedAtTouched;
    String requestedKeyword;
    PageOffset requestedPageOffset;
    private long nextId = 1;

    @Override
    public Optional<Event> findById(Long id) {
        return Optional.ofNullable(events.get(id));
    }

    @Override
    public Event save(Event event) {
        Long id = event.getId() == null ? nextId++ : event.getId();
        Event saved = Event.of(
            id, event.getTitle(), event.getDescription(), event.getPlace(), event.getEventStartAt(),
            event.getEventEndAt(), event.getApplyStartAt(), event.getApplyEndAt(), event.getRecruitType(),
            event.getImageIds(), event.getCapacity(), event.isFeePaidOnly(), event.isPublished(),
            event.getCreatedBy(), event.getUpdatedAt());
        events.put(id, saved);
        return saved;
    }

    @Override
    public boolean delete(Long id) {
        return events.remove(id) != null;
    }

    @Override
    public List<EventQuestion> saveQuestions(List<EventQuestion> saved) {
        saved.forEach(question ->
            questions.computeIfAbsent(question.getEventId(), key -> new ArrayList<>()).add(question));
        return saved;
    }

    @Override
    public List<EventQuestion> replaceQuestions(Long eventId, List<EventQuestion> replaced) {
        questionsReplaced = true;
        questions.put(eventId, replaced);
        return replaced;
    }

    @Override
    public boolean existsApplicationByEventId(Long eventId) {
        return applicationExists;
    }

    @Override
    public void touchUpdatedAt(Long eventId) {
        updatedAtTouched = true;
    }

    @Override
    public PageResult<Event> findPage(String keyword, PageOffset pageOffset) {
        requestedKeyword = keyword;
        requestedPageOffset = pageOffset;
        List<Event> content = List.copyOf(events.values());
        return PageResult.of(content, pageOffset.page(), pageOffset.size(), content.size());
    }

    @Override
    public List<EventQuestion> findQuestionsByEventId(Long eventId) {
        return questions.getOrDefault(eventId, List.of());
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
    public PageResult<EventApplication> findApplicationPage(
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
    public Optional<EventApplication> findApplicationByIdAndEventId(Long applicationId, Long eventId) {
        return Optional.ofNullable(applications.get(applicationId))
            .filter(application -> application.getEventId().equals(eventId));
    }

    @Override
    public long countApplicationsByEventId(Long eventId) {
        return applications.values().stream().filter(application -> application.getEventId().equals(eventId)).count();
    }

    @Override
    public List<Event> findAllByIds(List<Long> eventIds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Event> findPublishedById(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Event> findPublishedSlice(RecruitStatus recruitStatus, EventCursor cursor, int limit, LocalDateTime now) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean existsAppliedByEventIdAndMemberId(Long eventId, Long memberId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public EventApplication saveApplication(EventApplication application) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<EventApplicationAnswer> saveAnswers(List<EventApplicationAnswer> saved) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<EventApplication> findApplicationSlice(Long memberId, EventApplicationCursor cursor, int limit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<EventApplication> findApplicationByIdAndMemberId(Long applicationId, Long memberId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<EventApplicationAnswer> findAnswersByApplicationId(Long applicationId) {
        throw new UnsupportedOperationException();
    }
}
