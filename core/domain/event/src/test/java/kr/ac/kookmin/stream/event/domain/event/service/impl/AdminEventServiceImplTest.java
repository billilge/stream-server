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
import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.event.domain.Event;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCreateCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.EventManageDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventPeriod;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestionCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventUpdateCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.QuestionType;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitPolicy;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 운영진 행사 등록·조회·수정. 형식 규칙 자체는 {@code EventQuestionTest}·{@code EventPeriodTest}·
 * {@code RecruitPolicyTest}가 덮고,
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
    private final InMemoryEventApplicationRepository applicationRepository = new InMemoryEventApplicationRepository();
    private final AdminEventServiceImpl service =
        new AdminEventServiceImpl(repository, applicationRepository);

    private static Event savedEvent(RecruitType recruitType, int capacity) {
        return Event.of(
            EVENT_ID, "체육대회", "설명", "종합운동장", EVENT_START_AT, EVENT_END_AT, APPLY_START_AT, APPLY_END_AT,
            recruitType, List.of(31L), capacity, false, true, 7L, UPDATED_AT
        );
    }

    private static EventCreateCommand createCommand(
        RecruitType recruitType, Integer capacity, List<EventQuestionCommand> questions) {
        return new EventCreateCommand(
            "체육대회", "설명", "종합운동장", EventPeriod.of(EVENT_START_AT, EVENT_END_AT),
            EventPeriod.of(APPLY_START_AT, APPLY_END_AT), RecruitPolicy.of(recruitType, capacity, false), List.of(31L),
            questions
        );
    }

    private static EventUpdateCommand updateCommand(
        RecruitType recruitType, Integer capacity, List<EventQuestionCommand> questions, LocalDateTime expectedUpdatedAt) {
        return new EventUpdateCommand(
            "체육대회(변경)", "설명", "미래관", EventPeriod.of(EVENT_START_AT, EVENT_END_AT),
            EventPeriod.of(APPLY_START_AT, APPLY_END_AT), RecruitPolicy.of(recruitType, capacity, true), List.of(40L),
            questions, expectedUpdatedAt
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
    }

    @Nested
    @DisplayName("상세 조회")
    class GetEvent {

        @Test
        @DisplayName("행사·질문·유효 신청자 수를 함께 돌려준다")
        void returnsBundle() {
            repository.events.put(EVENT_ID, savedEvent(RecruitType.FIRST_COME, 100));
            repository.questions.put(EVENT_ID, savedQuestions(PHONE, SPORTS));
            applicationRepository.appliedCounts.put(EVENT_ID, 3L);

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
        }

        @Nested
        @DisplayName("정원 하한")
        class CapacityFloor {

            @Test
            @DisplayName("선착순 정원을 현재 신청자 수보다 줄일 수 없다")
            void rejectsBelowApplicants() {
                givenEvent(RecruitType.FIRST_COME, 100, savedQuestions(SPORTS));
                applicationRepository.appliedCounts.put(EVENT_ID, 10L);

                assertRejected(EventErrorCode.EVENT_CAPACITY_BELOW_APPLICANTS, () -> service.update(EVENT_ID,
                    updateCommand(RecruitType.FIRST_COME, 9, List.of(SPORTS), UPDATED_AT)));
            }

            @Test
            @DisplayName("신청자 수와 같은 정원까지는 줄일 수 있다")
            void allowsEqualToApplicants() {
                givenEvent(RecruitType.FIRST_COME, 100, savedQuestions(SPORTS));
                applicationRepository.appliedCounts.put(EVENT_ID, 10L);

                service.update(EVENT_ID, updateCommand(RecruitType.FIRST_COME, 10, List.of(SPORTS), UPDATED_AT));

                assertEquals(10, repository.events.get(EVENT_ID).getCapacity());
            }

            @Test
            @DisplayName("상시 모집에서 선착순으로 바꿀 때도 신청자 수 이상이어야 한다")
            void switchingToFirstComeChecksApplicants() {
                givenEvent(RecruitType.OPEN, 0, savedQuestions(SPORTS));
                applicationRepository.appliedCounts.put(EVENT_ID, 30L);

                assertRejected(EventErrorCode.EVENT_CAPACITY_BELOW_APPLICANTS, () -> service.update(EVENT_ID,
                    updateCommand(RecruitType.FIRST_COME, 20, List.of(SPORTS), UPDATED_AT)));
            }

            @Test
            @DisplayName("상시 모집은 신청자 수와 상관없이 수정할 수 있다")
            void openRecruitIgnoresApplicants() {
                givenEvent(RecruitType.FIRST_COME, 100, savedQuestions(SPORTS));
                applicationRepository.appliedCounts.put(EVENT_ID, 300L);

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
                applicationRepository.applicationExists = true;

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
                applicationRepository.applicationExists = true;

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
            applicationRepository.appliedCounts.put(1L, 3L);
            applicationRepository.appliedCounts.put(2L, 5L);

            assertEquals(Map.of(1L, 3L, 2L, 5L), service.getApplicantCounts(List.of(1L, 2L)));
        }
    }

    @Test
    @DisplayName("상세 조회의 모집 상태 계산에 쓰는 신청자 수는 유효 신청만 센다")
    void detailApplicantCountFeedsRecruitStatus() {
        repository.events.put(EVENT_ID, savedEvent(RecruitType.FIRST_COME, 3));
        repository.questions.put(EVENT_ID, List.of());
        applicationRepository.appliedCounts.put(EVENT_ID, 3L);

        EventManageDetail detail = service.getEvent(EVENT_ID);

        assertEquals(RecruitStatus.CLOSED,
            detail.event().calculateRecruitStatus(APPLY_START_AT.plusDays(1), detail.applicantCount()));
    }

    @Nested
    @DisplayName("공개 상태 변경")
    class ChangePublished {

        @Test
        @DisplayName("공개 여부를 바꿔 저장하고 다른 내용은 그대로 둔다")
        void changesOnlyPublished() {
            repository.events.put(EVENT_ID, savedEvent(RecruitType.FIRST_COME, 100));

            Event changed = service.changePublished(EVENT_ID, false);

            assertFalse(changed.isPublished());
            assertFalse(repository.events.get(EVENT_ID).isPublished());
            assertEquals("체육대회", repository.events.get(EVENT_ID).getTitle());
        }

        @Test
        @DisplayName("이미 그 상태여도 성공으로 본다")
        void idempotent() {
            repository.events.put(EVENT_ID, savedEvent(RecruitType.FIRST_COME, 100));

            assertTrue(service.changePublished(EVENT_ID, true).isPublished());
        }

        @Test
        @DisplayName("없거나 삭제된 행사는 찾을 수 없다")
        void notFound() {
            assertRejected(EventErrorCode.EVENT_NOT_FOUND, () -> service.changePublished(EVENT_ID, true));
        }
    }

    @Nested
    @DisplayName("삭제")
    class Delete {

        @Test
        @DisplayName("행사를 지운다")
        void deletes() {
            repository.events.put(EVENT_ID, savedEvent(RecruitType.FIRST_COME, 100));

            service.delete(EVENT_ID);

            assertTrue(repository.findById(EVENT_ID).isEmpty());
        }

        @Test
        @DisplayName("신청자가 있어도 지울 수 있다")
        void deletesEvenWithApplications() {
            repository.events.put(EVENT_ID, savedEvent(RecruitType.FIRST_COME, 100));
            applicationRepository.applicationExists = true;
            applicationRepository.appliedCounts.put(EVENT_ID, 12L);

            assertDoesNotThrow(() -> service.delete(EVENT_ID));
        }

        @Test
        @DisplayName("없거나 이미 삭제된 행사는 찾을 수 없다")
        void notFound() {
            assertRejected(EventErrorCode.EVENT_NOT_FOUND, () -> service.delete(EVENT_ID));
        }
    }
}
