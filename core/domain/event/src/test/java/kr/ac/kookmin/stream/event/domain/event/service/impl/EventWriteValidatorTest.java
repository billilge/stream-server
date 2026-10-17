package kr.ac.kookmin.stream.event.domain.event.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCreateCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestionCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventUpdateCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.QuestionType;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EventWriteValidatorTest {

    private static final LocalDateTime APPLY_START_AT = LocalDateTime.of(2026, 10, 14, 10, 0);
    private static final LocalDateTime APPLY_END_AT = LocalDateTime.of(2026, 10, 25, 23, 59, 59);
    private static final LocalDateTime EVENT_START_AT = LocalDateTime.of(2026, 11, 1, 10, 0);
    private static final LocalDateTime EVENT_END_AT = LocalDateTime.of(2026, 11, 1, 18, 0);

    private final EventWriteValidator validator = new EventWriteValidator();

    private static EventCreateCommand createCommand(
        LocalDateTime eventStartAt,
        LocalDateTime eventEndAt,
        LocalDateTime applyStartAt,
        LocalDateTime applyEndAt,
        RecruitType recruitType,
        Integer capacity,
        List<EventQuestionCommand> questions
    ) {
        return new EventCreateCommand(
            "체육대회", "설명", "종합운동장", eventStartAt, eventEndAt, applyStartAt, applyEndAt,
            recruitType, capacity, false, List.of(), questions
        );
    }

    private static EventCreateCommand validCommand() {
        return createCommand(
            EVENT_START_AT, EVENT_END_AT, APPLY_START_AT, APPLY_END_AT, RecruitType.FIRST_COME, 100, List.of());
    }

    private static EventQuestionCommand question(QuestionType type, List<String> options) {
        return new EventQuestionCommand("질문", type, true, options, false);
    }

    private static void assertRejected(EventErrorCode expected, Runnable validation) {
        BusinessException e = assertThrows(BusinessException.class, validation::run);
        assertEquals(expected, e.getErrorCode());
    }

    @Test
    @DisplayName("규칙을 모두 지키면 통과한다")
    void validCommandPasses() {
        assertDoesNotThrow(() -> validator.validate(validCommand()));
    }

    @Nested
    @DisplayName("기간")
    class Period {

        @Test
        @DisplayName("행사 종료가 시작보다 빠르면 거부한다")
        void eventEndsBeforeStart() {
            assertRejected(EventErrorCode.EVENT_INVALID_PERIOD, () -> validator.validate(createCommand(
                EVENT_START_AT, EVENT_START_AT.minusMinutes(1), APPLY_START_AT, APPLY_END_AT,
                RecruitType.OPEN, null, List.of())));
        }

        @Test
        @DisplayName("행사 시작과 종료가 같으면 거부한다")
        void eventEndsAtStart() {
            assertRejected(EventErrorCode.EVENT_INVALID_PERIOD, () -> validator.validate(createCommand(
                EVENT_START_AT, EVENT_START_AT, APPLY_START_AT, APPLY_END_AT, RecruitType.OPEN, null, List.of())));
        }

        @Test
        @DisplayName("신청 마감이 시작보다 빠르면 거부한다")
        void applyEndsBeforeStart() {
            assertRejected(EventErrorCode.EVENT_INVALID_PERIOD, () -> validator.validate(createCommand(
                EVENT_START_AT, EVENT_END_AT, APPLY_START_AT, APPLY_START_AT.minusSeconds(1),
                RecruitType.OPEN, null, List.of())));
        }

        @Test
        @DisplayName("신청 기간과 행사 기간이 겹쳐도 각각 순서만 맞으면 통과한다")
        void overlappingPeriodsAreAllowed() {
            assertDoesNotThrow(() -> validator.validate(createCommand(
                APPLY_START_AT.plusDays(1), APPLY_END_AT.plusDays(1), APPLY_START_AT, APPLY_END_AT,
                RecruitType.OPEN, null, List.of())));
        }
    }

    @Nested
    @DisplayName("정원")
    class Capacity {

        @Test
        @DisplayName("선착순인데 정원이 없으면 거부한다")
        void firstComeWithoutCapacity() {
            assertRejected(EventErrorCode.EVENT_INVALID_CAPACITY, () -> validator.validate(createCommand(
                EVENT_START_AT, EVENT_END_AT, APPLY_START_AT, APPLY_END_AT, RecruitType.FIRST_COME, null, List.of())));
        }

        @Test
        @DisplayName("선착순인데 정원이 0명이면 거부한다")
        void firstComeWithZeroCapacity() {
            assertRejected(EventErrorCode.EVENT_INVALID_CAPACITY, () -> validator.validate(createCommand(
                EVENT_START_AT, EVENT_END_AT, APPLY_START_AT, APPLY_END_AT, RecruitType.FIRST_COME, 0, List.of())));
        }

        @Test
        @DisplayName("선착순 정원은 1명부터 허용한다")
        void firstComeWithOneCapacity() {
            assertDoesNotThrow(() -> validator.validate(createCommand(
                EVENT_START_AT, EVENT_END_AT, APPLY_START_AT, APPLY_END_AT, RecruitType.FIRST_COME, 1, List.of())));
        }

        @Test
        @DisplayName("상시 모집은 정원을 보지 않는다")
        void openRecruitIgnoresCapacity() {
            assertDoesNotThrow(() -> validator.validate(createCommand(
                EVENT_START_AT, EVENT_END_AT, APPLY_START_AT, APPLY_END_AT, RecruitType.OPEN, null, List.of())));
            assertDoesNotThrow(() -> validator.validate(createCommand(
                EVENT_START_AT, EVENT_END_AT, APPLY_START_AT, APPLY_END_AT, RecruitType.OPEN, -1, List.of())));
        }
    }

    @Nested
    @DisplayName("신청서 질문")
    class Questions {

        @Test
        @DisplayName("선택형 질문에 선택지가 없으면 거부한다")
        void choiceWithoutOptions() {
            for (QuestionType type : List.of(QuestionType.SINGLE_CHOICE, QuestionType.MULTIPLE_CHOICE)) {
                assertRejected(EventErrorCode.EVENT_INVALID_QUESTION, () -> validator.validate(createCommand(
                    EVENT_START_AT, EVENT_END_AT, APPLY_START_AT, APPLY_END_AT, RecruitType.OPEN, null,
                    List.of(question(type, List.of())))));
                assertRejected(EventErrorCode.EVENT_INVALID_QUESTION, () -> validator.validate(createCommand(
                    EVENT_START_AT, EVENT_END_AT, APPLY_START_AT, APPLY_END_AT, RecruitType.OPEN, null,
                    List.of(question(type, null)))));
            }
        }

        @Test
        @DisplayName("여러 질문 중 하나라도 규칙을 어기면 거부한다")
        void oneInvalidAmongMany() {
            assertRejected(EventErrorCode.EVENT_INVALID_QUESTION, () -> validator.validate(createCommand(
                EVENT_START_AT, EVENT_END_AT, APPLY_START_AT, APPLY_END_AT, RecruitType.OPEN, null, List.of(
                    question(QuestionType.SHORT_TEXT, List.of()),
                    question(QuestionType.SINGLE_CHOICE, List.of("참석", "불참")),
                    question(QuestionType.MULTIPLE_CHOICE, List.of())))));
        }

        @Test
        @DisplayName("텍스트형 질문은 선택지가 없어도 통과한다")
        void textWithoutOptions() {
            assertDoesNotThrow(() -> validator.validate(createCommand(
                EVENT_START_AT, EVENT_END_AT, APPLY_START_AT, APPLY_END_AT, RecruitType.OPEN, null, List.of(
                    question(QuestionType.SHORT_TEXT, null),
                    question(QuestionType.LONG_TEXT, List.of())))));
        }
    }

    @Nested
    @DisplayName("수정 요청")
    class UpdateCommand {

        @Test
        @DisplayName("등록과 같은 규칙으로 검증한다")
        void sameRulesAsCreate() {
            EventUpdateCommand invalidPeriod = new EventUpdateCommand(
                "체육대회", "설명", null, EVENT_START_AT, EVENT_START_AT, APPLY_START_AT, APPLY_END_AT,
                RecruitType.OPEN, null, false, List.of(), List.of(), LocalDateTime.now());
            EventUpdateCommand invalidCapacity = new EventUpdateCommand(
                "체육대회", "설명", null, EVENT_START_AT, EVENT_END_AT, APPLY_START_AT, APPLY_END_AT,
                RecruitType.FIRST_COME, 0, false, List.of(), List.of(), LocalDateTime.now());
            EventUpdateCommand invalidQuestion = new EventUpdateCommand(
                "체육대회", "설명", null, EVENT_START_AT, EVENT_END_AT, APPLY_START_AT, APPLY_END_AT,
                RecruitType.OPEN, null, false, List.of(), List.of(question(QuestionType.SINGLE_CHOICE, List.of())),
                LocalDateTime.now());

            assertRejected(EventErrorCode.EVENT_INVALID_PERIOD, () -> validator.validate(invalidPeriod));
            assertRejected(EventErrorCode.EVENT_INVALID_CAPACITY, () -> validator.validate(invalidCapacity));
            assertRejected(EventErrorCode.EVENT_INVALID_QUESTION, () -> validator.validate(invalidQuestion));
        }
    }
}
