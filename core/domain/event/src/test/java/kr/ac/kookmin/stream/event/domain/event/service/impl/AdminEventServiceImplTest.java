package kr.ac.kookmin.stream.event.domain.event.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationAnswer;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCreateCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCursor;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.EventManageDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestionCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventUpdateCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.QuestionType;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitType;
import kr.ac.kookmin.stream.event.domain.event.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 운영진 행사 등록·조회·수정. 형식 규칙 자체는 {@code EventWriteValidatorTest}가 덮고,
 * 여기서는 저장 흐름과 수정 시의 규칙(동시 수정·정원 하한·질문 잠금)을 본다.
 */
class AdminEventServiceImplTest {

    private static final Long EVENT_ID = 15L;
    private static final LocalDateTime UPDATED_AT = LocalDateTime.of(2026, 10, 10, 12, 30, 15);
    private static final LocalDateTime APPLY_START_AT = LocalDateTime.of(2026, 10, 14, 10, 0);
    private static final LocalDateTime APPLY_END_AT = LocalDateTime.of(2026, 10, 25, 23, 59, 59);
    private static final LocalDateTime EVENT_START_AT = LocalDateTime.of(2026, 11, 1, 10, 0);
    private static final LocalDateTime EVENT_END_AT = LocalDateTime.of(2026, 11, 1, 18, 0);

    private static final EventQuestionCommand SPORTS = new EventQuestionCommand(
        "참여 종목", QuestionType.MULTIPLE_CHOICE, true, List.of("축구", "농구"), true);
    private static final EventQuestionCommand PHONE = new EventQuestionCommand(
        "전화번호", QuestionType.SHORT_TEXT, true, List.of(), false);

    private final InMemoryEventRepository repository = new InMemoryEventRepository();
    private final AdminEventServiceImpl service = new AdminEventServiceImpl(repository, new EventWriteValidator());

    private static Event savedEvent(RecruitType recruitType, int capacity) {
        return Event.of(
            EVENT_ID, "체육대회", "설명", "종합운동장", EVENT_START_AT, EVENT_END_AT, APPLY_START_AT, APPLY_END_AT,
            recruitType, List.of(31L), capacity, false, true, 7L, UPDATED_AT
        );
    }

    private static EventCreateCommand createCommand(
        RecruitType recruitType, Integer capacity, List<EventQuestionCommand> questions) {
        return new EventCreateCommand(
            "체육대회", "설명", "종합운동장", EVENT_START_AT, EVENT_END_AT, APPLY_START_AT, APPLY_END_AT,
            recruitType, capacity, false, List.of(31L), questions
        );
    }

    private static EventUpdateCommand updateCommand(
        RecruitType recruitType, Integer capacity, List<EventQuestionCommand> questions, LocalDateTime expectedUpdatedAt) {
        return new EventUpdateCommand(
            "체육대회(변경)", "설명", "미래관", EVENT_START_AT, EVENT_END_AT, APPLY_START_AT, APPLY_END_AT,
            recruitType, capacity, true, List.of(40L), questions, expectedUpdatedAt
        );
    }

    private static EventUpdateCommand updateCommand(List<EventQuestionCommand> questions) {
        return updateCommand(RecruitType.FIRST_COME, 100, questions, UPDATED_AT);
    }

    /** 저장된 질문처럼 id가 붙은 질문 목록. 노출 순서는 1부터 센다. */
    private static List<EventQuestion> savedQuestions(EventQuestionCommand... commands) {
        List<EventQuestion> questions = new ArrayList<>();
        for (int i = 0; i < commands.length; i++) {
            EventQuestionCommand command = commands[i];
            questions.add(EventQuestion.of(
                100L + i, EVENT_ID, command.questionText(), command.questionType(), command.required(), i + 1,
                command.options(), command.otherOptionAllowed()));
        }
        return questions;
    }

    private static void assertRejected(EventErrorCode expected, Runnable action) {
        BusinessException e = assertThrows(BusinessException.class, action::run);
        assertEquals(expected, e.getErrorCode());
    }

    @Nested
    @DisplayName("등록")
    class Create {

        @Test
        @DisplayName("행사를 비공개로 저장하고 작성자를 기록한다")
        void savesUnpublishedEvent() {
            Event created = service.create(7L, createCommand(RecruitType.FIRST_COME, 100, List.of()));

            Event saved = repository.events.get(created.getId());
            assertFalse(saved.isPublished());
            assertEquals(7L, saved.getCreatedBy());
            assertEquals(100, saved.getCapacity());
        }

        @Test
        @DisplayName("질문을 요청 순서대로 1부터 노출 순서를 붙여 저장한다")
        void savesQuestionsInRequestOrder() {
            Event created = service.create(7L, createCommand(RecruitType.OPEN, null, List.of(PHONE, SPORTS)));

            List<EventQuestion> questions = repository.questions.get(created.getId());
            assertEquals(List.of("전화번호", "참여 종목"),
                questions.stream().map(EventQuestion::getQuestionText).toList());
            assertEquals(List.of(1, 2), questions.stream().map(EventQuestion::getDisplayOrder).toList());
            assertTrue(questions.stream().allMatch(question -> question.getEventId().equals(created.getId())));
        }

        @Test
        @DisplayName("질문이 없으면 빈 신청서로 저장한다")
        void savesEmptyForm() {
            Event created = service.create(7L, createCommand(RecruitType.OPEN, null, List.of()));

            assertTrue(repository.findQuestionsByEventId(created.getId()).isEmpty());
        }

        @Test
        @DisplayName("규칙을 어기면 아무것도 저장하지 않는다")
        void savesNothingWhenInvalid() {
            assertRejected(EventErrorCode.EVENT_INVALID_CAPACITY,
                () -> service.create(7L, createCommand(RecruitType.FIRST_COME, 0, List.of(SPORTS))));

            assertTrue(repository.events.isEmpty());
            assertTrue(repository.questions.isEmpty());
        }
    }

    @Nested
    @DisplayName("상세 조회")
    class GetEvent {

        @Test
        @DisplayName("행사·질문·유효 신청자 수를 함께 돌려준다")
        void returnsBundle() {
            repository.events.put(EVENT_ID, savedEvent(RecruitType.FIRST_COME, 100));
            repository.questions.put(EVENT_ID, savedQuestions(PHONE, SPORTS));
            repository.appliedCounts.put(EVENT_ID, 3L);

            EventManageDetail detail = service.getEvent(EVENT_ID);

            assertEquals(EVENT_ID, detail.event().getId());
            assertEquals(2, detail.questions().size());
            assertEquals(3L, detail.applicantCount());
        }

        @Test
        @DisplayName("없거나 삭제된 행사는 찾을 수 없다")
        void notFound() {
            assertRejected(EventErrorCode.EVENT_NOT_FOUND, () -> service.getEvent(EVENT_ID));
        }
    }

    @Nested
    @DisplayName("수정")
    class Update {

        private void givenEvent(RecruitType recruitType, int capacity, List<EventQuestion> questions) {
            repository.events.put(EVENT_ID, savedEvent(recruitType, capacity));
            repository.questions.put(EVENT_ID, questions);
        }

        @Test
        @DisplayName("내용을 바꿔 저장하고 공개 여부·작성자는 그대로 둔다")
        void updatesContent() {
            givenEvent(RecruitType.FIRST_COME, 100, savedQuestions(SPORTS));

            service.update(EVENT_ID, updateCommand(List.of(SPORTS)));

            Event saved = repository.events.get(EVENT_ID);
            assertEquals("체육대회(변경)", saved.getTitle());
            assertEquals("미래관", saved.getPlace());
            assertEquals(List.of(40L), saved.getImageIds());
            assertTrue(saved.isFeePaidOnly());
            assertTrue(saved.isPublished());
            assertEquals(7L, saved.getCreatedBy());
        }

        @Test
        @DisplayName("없거나 삭제된 행사는 찾을 수 없다")
        void notFound() {
            assertRejected(EventErrorCode.EVENT_NOT_FOUND, () -> service.update(EVENT_ID, updateCommand(List.of())));
        }

        @Nested
        @DisplayName("동시 수정")
        class Conflict {

            @Test
            @DisplayName("수정 화면이 읽은 뒤 다른 운영진이 고쳤다면 거부한다")
            void rejectsWhenModifiedMeanwhile() {
                givenEvent(RecruitType.FIRST_COME, 100, savedQuestions(SPORTS));

                assertRejected(EventErrorCode.EVENT_UPDATE_CONFLICT, () -> service.update(EVENT_ID,
                    updateCommand(RecruitType.FIRST_COME, 100, List.of(SPORTS), UPDATED_AT.minusSeconds(1))));
                assertEquals("체육대회", repository.events.get(EVENT_ID).getTitle());
            }

            @Test
            @DisplayName("같은 초 안의 밀리초 차이는 같은 시각으로 본다 — DB가 초 단위로 저장한다")
            void ignoresSubSecondDifference() {
                givenEvent(RecruitType.FIRST_COME, 100, savedQuestions(SPORTS));

                assertDoesNotThrow(() -> service.update(EVENT_ID,
                    updateCommand(RecruitType.FIRST_COME, 100, List.of(SPORTS), UPDATED_AT.plusNanos(999_000_000))));
            }

            @Test
            @DisplayName("동시 수정 검사는 형식 검증보다 먼저 한다")
            void checkedBeforeFormat() {
                givenEvent(RecruitType.FIRST_COME, 100, savedQuestions(SPORTS));

                assertRejected(EventErrorCode.EVENT_UPDATE_CONFLICT, () -> service.update(EVENT_ID,
                    updateCommand(RecruitType.FIRST_COME, 0, List.of(SPORTS), UPDATED_AT.minusDays(1))));
            }
        }

        @Nested
        @DisplayName("정원 하한")
        class CapacityFloor {

            @Test
            @DisplayName("선착순 정원을 현재 신청자 수보다 줄일 수 없다")
            void rejectsBelowApplicants() {
                givenEvent(RecruitType.FIRST_COME, 100, savedQuestions(SPORTS));
                repository.appliedCounts.put(EVENT_ID, 10L);

                assertRejected(EventErrorCode.EVENT_CAPACITY_BELOW_APPLICANTS, () -> service.update(EVENT_ID,
                    updateCommand(RecruitType.FIRST_COME, 9, List.of(SPORTS), UPDATED_AT)));
            }

            @Test
            @DisplayName("신청자 수와 같은 정원까지는 줄일 수 있다")
            void allowsEqualToApplicants() {
                givenEvent(RecruitType.FIRST_COME, 100, savedQuestions(SPORTS));
                repository.appliedCounts.put(EVENT_ID, 10L);

                service.update(EVENT_ID, updateCommand(RecruitType.FIRST_COME, 10, List.of(SPORTS), UPDATED_AT));

                assertEquals(10, repository.events.get(EVENT_ID).getCapacity());
            }

            @Test
            @DisplayName("상시 모집에서 선착순으로 바꿀 때도 신청자 수 이상이어야 한다")
            void switchingToFirstComeChecksApplicants() {
                givenEvent(RecruitType.OPEN, 0, savedQuestions(SPORTS));
                repository.appliedCounts.put(EVENT_ID, 30L);

                assertRejected(EventErrorCode.EVENT_CAPACITY_BELOW_APPLICANTS, () -> service.update(EVENT_ID,
                    updateCommand(RecruitType.FIRST_COME, 20, List.of(SPORTS), UPDATED_AT)));
            }

            @Test
            @DisplayName("상시 모집은 신청자 수와 상관없이 수정할 수 있다")
            void openRecruitIgnoresApplicants() {
                givenEvent(RecruitType.FIRST_COME, 100, savedQuestions(SPORTS));
                repository.appliedCounts.put(EVENT_ID, 300L);

                assertDoesNotThrow(() -> service.update(EVENT_ID,
                    updateCommand(RecruitType.OPEN, null, List.of(SPORTS), UPDATED_AT)));
            }
        }

        @Nested
        @DisplayName("신청서 질문")
        class Questions {

            @Test
            @DisplayName("질문이 그대로면 교체하지 않아 질문 id가 유지된다")
            void keepsQuestionsWhenUnchanged() {
                List<EventQuestion> original = savedQuestions(PHONE, SPORTS);
                givenEvent(RecruitType.FIRST_COME, 100, original);
                repository.applicationExists = true;

                service.update(EVENT_ID, updateCommand(List.of(PHONE, SPORTS)));

                assertFalse(repository.questionsReplaced);
                assertFalse(repository.updatedAtTouched);
                assertSame(original, repository.questions.get(EVENT_ID));
            }

            @Test
            @DisplayName("신청이 없으면 바뀐 질문으로 통째로 교체한다")
            void replacesWhenNoApplication() {
                givenEvent(RecruitType.FIRST_COME, 100, savedQuestions(PHONE, SPORTS));

                service.update(EVENT_ID, updateCommand(List.of(SPORTS)));

                assertTrue(repository.questionsReplaced);
                List<EventQuestion> replaced = repository.questions.get(EVENT_ID);
                assertEquals(List.of("참여 종목"), replaced.stream().map(EventQuestion::getQuestionText).toList());
                assertEquals(1, replaced.getFirst().getDisplayOrder());
                assertNull(replaced.getFirst().getId());
            }

            @Test
            @DisplayName("질문을 교체하면 행사 수정 시각도 올린다 — 질문만 바뀌면 DB가 시각을 올리지 않는다")
            void touchesUpdatedAtWhenReplaced() {
                givenEvent(RecruitType.FIRST_COME, 100, savedQuestions(SPORTS));

                service.update(EVENT_ID, updateCommand(List.of(SPORTS, PHONE)));

                assertTrue(repository.updatedAtTouched);
            }

            @Test
            @DisplayName("순서만 바꿔도 바뀐 것으로 본다")
            void reorderCountsAsChange() {
                givenEvent(RecruitType.FIRST_COME, 100, savedQuestions(PHONE, SPORTS));

                service.update(EVENT_ID, updateCommand(List.of(SPORTS, PHONE)));

                assertTrue(repository.questionsReplaced);
            }

            @Test
            @DisplayName("신청(취소 포함)이 있으면 질문을 바꿀 수 없고 행사도 저장하지 않는다")
            void lockedWhenApplicationExists() {
                givenEvent(RecruitType.FIRST_COME, 100, savedQuestions(SPORTS));
                repository.applicationExists = true;

                assertRejected(EventErrorCode.EVENT_QUESTIONS_LOCKED,
                    () -> service.update(EVENT_ID, updateCommand(List.of(SPORTS, PHONE))));
                assertFalse(repository.questionsReplaced);
                assertEquals("체육대회", repository.events.get(EVENT_ID).getTitle());
            }
        }
    }

    @Nested
    @DisplayName("목록 조회")
    class GetEvents {

        @Test
        @DisplayName("검색어와 페이지를 그대로 넘겨 행사 페이지를 돌려준다")
        void delegatesToRepository() {
            repository.events.put(EVENT_ID, savedEvent(RecruitType.FIRST_COME, 100));

            PageResult<Event> page = service.getEvents("체육", PageOffset.of(1, 10));

            assertEquals("체육", repository.requestedKeyword);
            assertEquals(PageOffset.of(1, 10), repository.requestedPageOffset);
            assertEquals(1, page.content().size());
        }

        @Test
        @DisplayName("신청자 수는 행사 id 목록으로 한 번에 조회한다")
        void countsApplicantsAtOnce() {
            repository.appliedCounts.put(1L, 3L);
            repository.appliedCounts.put(2L, 5L);

            assertEquals(Map.of(1L, 3L, 2L, 5L), service.getApplicantCounts(List.of(1L, 2L)));
        }
    }

    @Test
    @DisplayName("상세 조회의 모집 상태 계산에 쓰는 신청자 수는 유효 신청만 센다")
    void detailApplicantCountFeedsRecruitStatus() {
        repository.events.put(EVENT_ID, savedEvent(RecruitType.FIRST_COME, 3));
        repository.questions.put(EVENT_ID, List.of());
        repository.appliedCounts.put(EVENT_ID, 3L);

        EventManageDetail detail = service.getEvent(EVENT_ID);

        assertEquals(RecruitStatus.CLOSED,
            detail.event().calculateRecruitStatus(APPLY_START_AT.plusDays(1), detail.applicantCount()));
    }

    /** 운영진 등록·조회·수정에 쓰는 메서드만 메모리로 흉내 내고, 나머지는 이 테스트가 건드리지 않는다. */
    private static final class InMemoryEventRepository implements EventRepository {

        private final Map<Long, Event> events = new HashMap<>();
        private final Map<Long, List<EventQuestion>> questions = new HashMap<>();
        private final Map<Long, Long> appliedCounts = new HashMap<>();
        private boolean applicationExists;
        private boolean questionsReplaced;
        private boolean updatedAtTouched;
        private String requestedKeyword;
        private PageOffset requestedPageOffset;
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
        public List<Event> findAllByIds(List<Long> eventIds) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Optional<Event> findPublishedById(Long id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Event> findPublishedSlice(
            RecruitStatus recruitStatus, EventCursor cursor, int limit, LocalDateTime now) {
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

        @Override
        public List<Long> findAppliedMemberIds(Long eventId) {
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
}
