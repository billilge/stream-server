package kr.ac.kookmin.stream.event.domain.event.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import kr.ac.kookmin.stream.common.CursorSliceResult;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationAnswer;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.EventSummary;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitType;
import kr.ac.kookmin.stream.event.domain.event.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 행사 목록은 행사와 신청자 수를 각각 조회해 서비스에서 짝지으므로, 그 조합이 어긋나지 않는지 확인한다.
 */
class EventServiceImplTest {

    private static final LocalDateTime NOW = LocalDateTime.now();

    private static Event event(Long id) {
        return Event.of(
            id, "행사 " + id, "설명", "전교생", "101호",
            NOW.plusDays(10), NOW.plusDays(10).plusHours(2), NOW.minusDays(1), NOW.plusDays(3),
            RecruitType.FIRST_COME, List.of(), 100, RecruitStatus.OPEN, true, 1L
        );
    }

    @Test
    @DisplayName("행사마다 자기 신청자 수를 붙인다")
    void matchesApplicantCountToItsEvent() {
        FakeEventRepository repository = new FakeEventRepository(
            List.of(event(1L), event(2L)),
            Map.of(1L, 3L, 2L, 7L));

        List<EventSummary> content = new EventServiceImpl(repository, null)
            .getPublishedEvents(null, null, 20).content();

        // 정원 100명이라 신청자 수가 모집 상태를 바꾸지 않는다. 짝이 맞는지는 D-Day가 아니라 아래에서 직접 본다
        assertEquals(List.of(1L, 2L), content.stream().map(EventSummary::eventId).toList());
        assertEquals(List.of(1L, 2L), repository.requestedEventIds);
    }

    @Test
    @DisplayName("신청이 한 건도 없는 행사도 0명으로 채워 목록에서 빠지지 않는다")
    void keepsEventsWithoutApplicants() {
        // 집계 쿼리는 신청이 없는 행사를 아예 돌려주지 않으므로 2번 행사는 맵에 없다
        FakeEventRepository repository = new FakeEventRepository(
            List.of(event(1L), event(2L)),
            Map.of(1L, 3L));

        List<EventSummary> content = new EventServiceImpl(repository, null)
            .getPublishedEvents(null, null, 20).content();

        assertEquals(2, content.size());
        assertEquals(List.of(1L, 2L), content.stream().map(EventSummary::eventId).toList());
    }

    @Test
    @DisplayName("정원이 찬 행사만 마감으로 바뀐다")
    void appliesCountPerEvent() {
        Event full = Event.of(
            1L, "정원 3명", "설명", "전교생", "101호",
            NOW.plusDays(10), null, NOW.minusDays(1), NOW.plusDays(3),
            RecruitType.FIRST_COME, List.of(), 3, RecruitStatus.OPEN, true, 1L);
        Event roomy = Event.of(
            2L, "정원 100명", "설명", "전교생", "101호",
            NOW.plusDays(10), null, NOW.minusDays(1), NOW.plusDays(3),
            RecruitType.FIRST_COME, List.of(), 100, RecruitStatus.OPEN, true, 1L);

        FakeEventRepository repository = new FakeEventRepository(
            List.of(full, roomy),
            Map.of(1L, 3L, 2L, 3L));

        List<EventSummary> content = new EventServiceImpl(repository, null)
            .getPublishedEvents(null, null, 20).content();

        assertEquals(RecruitStatus.CLOSED, content.get(0).recruitStatus());
        assertEquals(RecruitStatus.OPEN, content.get(1).recruitStatus());
    }

    @Test
    @DisplayName("요청한 크기보다 한 건 더 읽어 다음 페이지 여부를 판단한다")
    void detectsNextPage() {
        FakeEventRepository repository = new FakeEventRepository(
            List.of(event(1L), event(2L), event(3L)), Map.of());

        CursorSliceResult<EventSummary> result = new EventServiceImpl(repository, null)
            .getPublishedEvents(null, null, 2);

        assertEquals(3, repository.requestedLimit);
        assertEquals(List.of(1L, 2L), result.content().stream().map(EventSummary::eventId).toList());
        assertTrue(result.hasNext());
        assertEquals(EventCursor.of(event(2L)).format(), result.nextCursor());
    }

    @Test
    @DisplayName("마지막 페이지면 다음 커서를 내려보내지 않는다")
    void noCursorOnLastPage() {
        FakeEventRepository repository = new FakeEventRepository(List.of(event(1L), event(2L)), Map.of());

        CursorSliceResult<EventSummary> result = new EventServiceImpl(repository, null)
            .getPublishedEvents(null, null, 2);

        assertFalse(result.hasNext());
        assertNull(result.nextCursor());
    }

    @Test
    @DisplayName("조회 결과가 없으면 신청자 수도 빈 목록으로 물어본다")
    void asksNothingWhenSliceIsEmpty() {
        FakeEventRepository repository = new FakeEventRepository(
            List.of(), Map.of());

        CursorSliceResult<EventSummary> result = new EventServiceImpl(repository, null)
            .getPublishedEvents(null, null, 20);

        assertTrue(result.content().isEmpty());
        assertEquals(List.of(), repository.requestedEventIds);
    }

    /** 목록 조회에 쓰이는 두 메서드만 답하고, 나머지는 이 테스트가 건드리지 않는다. */
    private static final class FakeEventRepository implements EventRepository {

        private final List<Event> fetched;
        private final Map<Long, Long> applicantCounts;
        private List<Long> requestedEventIds;
        private int requestedLimit;

        private FakeEventRepository(List<Event> fetched, Map<Long, Long> applicantCounts) {
            this.fetched = fetched;
            this.applicantCounts = applicantCounts;
        }

        @Override
        public List<Event> findPublishedSlice(
            RecruitStatus recruitStatus, EventCursor cursor, int limit, LocalDateTime now) {
            this.requestedLimit = limit;
            return fetched.stream().limit(limit).toList();
        }

        @Override
        public Map<Long, Long> countAppliedByEventIds(List<Long> eventIds) {
            this.requestedEventIds = eventIds;
            return applicantCounts;
        }

        @Override
        public Optional<Event> findById(Long id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Event> findPublishedById(Long id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<EventQuestion> findQuestionsByEventId(Long eventId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long countAppliedByEventId(Long eventId) {
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
        public List<EventApplicationAnswer> saveAnswers(List<EventApplicationAnswer> answers) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Event> findAllByIds(List<Long> eventIds) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<EventApplication> findApplicationSlice(
            Long memberId, EventApplicationCursor cursor, int limit) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<EventApplication> findApplicationByIdAndMemberId(
            Long applicationId, Long memberId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<EventApplicationAnswer> findAnswersByApplicationId(Long applicationId) {
            throw new UnsupportedOperationException();
        }
    }
}
