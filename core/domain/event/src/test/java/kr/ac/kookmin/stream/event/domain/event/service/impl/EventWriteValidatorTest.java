package kr.ac.kookmin.stream.event.domain.event.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.event.domain.EventCreateCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.EventPeriod;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestionCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventUpdateCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.QuestionType;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitPolicy;
import kr.ac.kookmin.stream.event.domain.event.domain.RecruitType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 기간 순서와 선착순 정원은 {@code EventPeriodTest}·{@code RecruitPolicyTest}가 덮고, 여기서는 신청서 질문 규칙을 본다.
 */
class EventWriteValidatorTest {

    private static final EventPeriod APPLY_PERIOD = EventPeriod.of(
        LocalDateTime.of(2026, 10, 14, 10, 0), LocalDateTime.of(2026, 10, 25, 23, 59, 59));
    private static final EventPeriod EVENT_PERIOD = EventPeriod.of(
        LocalDateTime.of(2026, 11, 1, 10, 0), LocalDateTime.of(2026, 11, 1, 18, 0));

    private final EventWriteValidator validator = new EventWriteValidator();

    private static EventCreateCommand createCommand(List<EventQuestionCommand> questions) {
        return new EventCreateCommand(
            "체육대회", "설명", "종합운동장", EVENT_PERIOD, APPLY_PERIOD, RecruitPolicy.of(RecruitType.FIRST_COME, 100),
            false, List.of(), questions
        );
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
        assertDoesNotThrow(() -> validator.validate(createCommand(List.of())));
    }

    @Nested
    @DisplayName("신청서 질문")
    class Questions {

        @Test
        @DisplayName("선택형 질문에 선택지가 없으면 거부한다")
        void choiceWithoutOptions() {
            for (QuestionType type : List.of(QuestionType.SINGLE_CHOICE, QuestionType.MULTIPLE_CHOICE)) {
                assertRejected(EventErrorCode.EVENT_INVALID_QUESTION,
                    () -> validator.validate(createCommand(List.of(question(type, List.of())))));
                assertRejected(EventErrorCode.EVENT_INVALID_QUESTION,
                    () -> validator.validate(createCommand(List.of(question(type, null)))));
            }
        }

        @Test
        @DisplayName("여러 질문 중 하나라도 규칙을 어기면 거부한다")
        void oneInvalidAmongMany() {
            assertRejected(EventErrorCode.EVENT_INVALID_QUESTION, () -> validator.validate(createCommand(List.of(
                question(QuestionType.SHORT_TEXT, List.of()),
                question(QuestionType.SINGLE_CHOICE, List.of("참석", "불참")),
                question(QuestionType.MULTIPLE_CHOICE, List.of())))));
        }

        @Test
        @DisplayName("텍스트형 질문은 선택지가 없어도 통과한다")
        void textWithoutOptions() {
            assertDoesNotThrow(() -> validator.validate(createCommand(List.of(
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
            EventUpdateCommand invalidQuestion = new EventUpdateCommand(
                "체육대회", "설명", null, EVENT_PERIOD, APPLY_PERIOD, RecruitPolicy.of(RecruitType.OPEN, null), false,
                List.of(), List.of(question(QuestionType.SINGLE_CHOICE, List.of())), LocalDateTime.now());

            assertRejected(EventErrorCode.EVENT_INVALID_QUESTION, () -> validator.validate(invalidQuestion));
        }
    }
}
