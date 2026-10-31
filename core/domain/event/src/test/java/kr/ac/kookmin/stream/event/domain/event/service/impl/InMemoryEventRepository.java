package kr.ac.kookmin.stream.event.domain.event.service.impl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;
import kr.ac.kookmin.stream.event.domain.event.repository.EventRepository;

/**
 * 운영진 행사 관리 테스트용 행사 메모리 레포지토리. 운영진 기능이 쓰는 메서드만 흉내 내고,
 * 학생 목록 쪽 메서드는 이 테스트들이 건드리지 않으므로 막아 둔다.
 */
class InMemoryEventRepository implements EventRepository {

    final Map<Long, Event> events = new HashMap<>();
    final Map<Long, List<EventQuestion>> questions = new HashMap<>();
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
    public void touchUpdatedAt(Long eventId) {
        updatedAtTouched = true;
    }

    @Override
    public PageResult<Event> search(String keyword, PageOffset pageOffset) {
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
    public List<Event> findAllByIds(List<Long> eventIds) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Event> findPublishedByIds(Collection<Long> eventIds) {
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
    public List<Event> findPublishedStartingBetween(LocalDateTime from, LocalDateTime to) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Event> findPublishedApplyEndingBetween(LocalDateTime from, LocalDateTime to) {
        throw new UnsupportedOperationException();
    }
}
