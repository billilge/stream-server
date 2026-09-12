package kr.ac.kookmin.stream.event.domain.event.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplyCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplyCommand.AnswerCommand;
import kr.ac.kookmin.stream.event.domain.event.domain.EventErrorCode;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.QuestionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EventApplyAnswerValidatorTest {

    private static final Long SHORT_TEXT_ID = 1L;
    private static final Long LONG_TEXT_ID = 2L;
    private static final Long SINGLE_CHOICE_ID = 3L;
    private static final Long MULTIPLE_CHOICE_ID = 4L;

    private final EventApplyAnswerValidator validator = new EventApplyAnswerValidator();

    private static EventQuestion question(Long id, QuestionType type, boolean required, List<String> options) {
        return EventQuestion.of(id, 1L, "질문", type, required, 0, options);
    }

    /** 단답(필수) / 장문(선택) / 객관식 3지(필수) / 체크박스 2지(선택) */
    private static List<EventQuestion> questions() {
        return List.of(
            question(SHORT_TEXT_ID, QuestionType.SHORT_TEXT, true, null),
            question(LONG_TEXT_ID, QuestionType.LONG_TEXT, false, null),
            question(SINGLE_CHOICE_ID, QuestionType.SINGLE_CHOICE, true, List.of("13시", "14시", "15시")),
            question(MULTIPLE_CHOICE_ID, QuestionType.MULTIPLE_CHOICE, false, List.of("레크리에이션", "네트워킹"))
        );
    }

    private static EventApplyCommand command(AnswerCommand... answers) {
        return new EventApplyCommand(List.of(answers));
    }

    private static AnswerCommand text(Long questionId, String answerText) {
        return new AnswerCommand(questionId, answerText, List.of());
    }

    private static AnswerCommand choice(Long questionId, Integer... selectedOptions) {
        return new AnswerCommand(questionId, null, List.of(selectedOptions));
    }

    private void assertInvalidAnswer(EventApplyCommand command) {
        BusinessException exception = assertThrows(
            BusinessException.class, () -> validator.validate(questions(), command)
        );
        assertEquals(EventErrorCode.INVALID_ANSWER, exception.getErrorCode());
    }

    @Test
    @DisplayName("필수 질문만 채우고 선택 질문을 생략하면 통과한다")
    void passWithRequiredOnly() {
        assertDoesNotThrow(() -> validator.validate(
            questions(),
            command(text(SHORT_TEXT_ID, "소프트웨어학부"), choice(SINGLE_CHOICE_ID, 0))
        ));
    }

    @Test
    @DisplayName("선택 질문을 빈 값으로 보내도 생략과 같게 통과한다")
    void passWithEmptyOptionalAnswer() {
        assertDoesNotThrow(() -> validator.validate(
            questions(),
            command(
                text(SHORT_TEXT_ID, "소프트웨어학부"),
                choice(SINGLE_CHOICE_ID, 0),
                text(LONG_TEXT_ID, "   "),
                new AnswerCommand(MULTIPLE_CHOICE_ID, null, List.of())
            )
        ));
    }

    @Test
    @DisplayName("선택형에 빈 문자열 answerText가 섞여 와도 통과한다")
    void passWhenChoiceCarriesBlankAnswerText() {
        assertDoesNotThrow(() -> validator.validate(
            questions(),
            command(
                text(SHORT_TEXT_ID, "소프트웨어학부"),
                new AnswerCommand(SINGLE_CHOICE_ID, "", List.of(0))
            )
        ));
    }

    @Test
    @DisplayName("필수 질문을 생략하면 실패한다")
    void rejectMissingRequired() {
        assertInvalidAnswer(command(text(SHORT_TEXT_ID, "소프트웨어학부")));
    }

    @Test
    @DisplayName("필수 질문을 빈 값으로 보내면 실패한다")
    void rejectBlankRequired() {
        assertInvalidAnswer(command(text(SHORT_TEXT_ID, "  "), choice(SINGLE_CHOICE_ID, 0)));
    }

    @Test
    @DisplayName("해당 행사의 질문이 아니면 실패한다")
    void rejectUnknownQuestion() {
        assertInvalidAnswer(command(
            text(SHORT_TEXT_ID, "소프트웨어학부"), choice(SINGLE_CHOICE_ID, 0), text(999L, "값")
        ));
    }

    @Test
    @DisplayName("같은 질문에 두 번 답하면 실패한다")
    void rejectDuplicateQuestion() {
        assertInvalidAnswer(command(
            text(SHORT_TEXT_ID, "소프트웨어학부"), text(SHORT_TEXT_ID, "인공지능학부"), choice(SINGLE_CHOICE_ID, 0)
        ));
    }

    @Test
    @DisplayName("단답형이 50자를 넘으면 실패한다")
    void rejectShortTextOverMaxLength() {
        assertInvalidAnswer(command(text(SHORT_TEXT_ID, "가".repeat(51)), choice(SINGLE_CHOICE_ID, 0)));
    }

    @Test
    @DisplayName("장문형은 500자까지 통과하고 501자는 실패한다")
    void longTextMaxLengthBoundary() {
        assertDoesNotThrow(() -> validator.validate(
            questions(),
            command(text(SHORT_TEXT_ID, "학부"), choice(SINGLE_CHOICE_ID, 0), text(LONG_TEXT_ID, "가".repeat(500)))
        ));
        assertInvalidAnswer(command(
            text(SHORT_TEXT_ID, "학부"), choice(SINGLE_CHOICE_ID, 0), text(LONG_TEXT_ID, "가".repeat(501))
        ));
    }

    @Test
    @DisplayName("주관식에 선택지가 함께 오면 실패한다")
    void rejectTextAnswerWithSelectedOptions() {
        assertInvalidAnswer(command(
            new AnswerCommand(SHORT_TEXT_ID, "학부", List.of(0)), choice(SINGLE_CHOICE_ID, 0)
        ));
    }

    @Test
    @DisplayName("객관식에 선택지를 두 개 고르면 실패한다")
    void rejectSingleChoiceWithMultipleOptions() {
        assertInvalidAnswer(command(text(SHORT_TEXT_ID, "학부"), choice(SINGLE_CHOICE_ID, 0, 1)));
    }

    @Test
    @DisplayName("체크박스에서 같은 선택지를 중복으로 고르면 실패한다")
    void rejectDuplicateSelectedOptions() {
        assertInvalidAnswer(command(
            text(SHORT_TEXT_ID, "학부"), choice(SINGLE_CHOICE_ID, 0), choice(MULTIPLE_CHOICE_ID, 1, 1)
        ));
    }

    @Test
    @DisplayName("선택지 인덱스가 범위를 벗어나면 실패한다")
    void rejectOutOfRangeSelectedOption() {
        assertInvalidAnswer(command(text(SHORT_TEXT_ID, "학부"), choice(SINGLE_CHOICE_ID, 3)));
        assertInvalidAnswer(command(text(SHORT_TEXT_ID, "학부"), choice(SINGLE_CHOICE_ID, -1)));
    }

    @Test
    @DisplayName("질문이 없는 행사는 빈 답변으로 통과한다")
    void passWhenNoQuestions() {
        assertDoesNotThrow(() -> validator.validate(List.of(), new EventApplyCommand(List.of())));
    }
}
