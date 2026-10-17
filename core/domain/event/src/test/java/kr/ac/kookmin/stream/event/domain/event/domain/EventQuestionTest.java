package kr.ac.kookmin.stream.event.domain.event.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EventQuestionTest {

    private static EventQuestionCommand command(QuestionType type, List<String> options, boolean otherOptionAllowed) {
        return new EventQuestionCommand("참여 종목", type, true, options, otherOptionAllowed);
    }

    private static EventQuestion saved(Long id, QuestionType type, List<String> options, boolean otherOptionAllowed) {
        return EventQuestion.of(id, 15L, "참여 종목", type, true, 1, options, otherOptionAllowed);
    }

    @Nested
    @DisplayName("새 질문 만들기")
    class Create {

        @Test
        @DisplayName("소속 행사와 노출 순서를 붙여 입력값을 담는다")
        void copiesCommand() {
            EventQuestion question = EventQuestion.create(
                15L, 2, command(QuestionType.MULTIPLE_CHOICE, List.of("축구", "농구"), true));

            assertNull(question.getId());
            assertEquals(15L, question.getEventId());
            assertEquals(2, question.getDisplayOrder());
            assertEquals("참여 종목", question.getQuestionText());
            assertEquals(QuestionType.MULTIPLE_CHOICE, question.getQuestionType());
            assertTrue(question.isRequired());
            assertEquals(List.of("축구", "농구"), question.getOptions());
            assertTrue(question.isOtherOptionAllowed());
        }

        @Test
        @DisplayName("텍스트형 질문에 딸려 온 선택지와 '기타' 여부는 버린다")
        void textQuestionDropsChoiceFields() {
            for (QuestionType type : List.of(QuestionType.SHORT_TEXT, QuestionType.LONG_TEXT)) {
                EventQuestion question = EventQuestion.create(15L, 1, command(type, List.of("축구"), true));

                assertEquals(List.of(), question.getOptions());
                assertFalse(question.isOtherOptionAllowed());
            }
        }

        @Test
        @DisplayName("요청 목록을 나중에 바꿔도 질문의 선택지는 바뀌지 않는다")
        void copiesOptions() {
            List<String> options = new ArrayList<>(List.of("축구"));
            EventQuestion question = EventQuestion.create(15L, 1, command(QuestionType.SINGLE_CHOICE, options, false));

            options.add("농구");

            assertEquals(List.of("축구"), question.getOptions());
        }
    }

    @Nested
    @DisplayName("내용 비교")
    class SameContent {

        @Test
        @DisplayName("식별자·소속 행사·노출 순서 값이 달라도 신청서에 보이는 내용이 같으면 같다")
        void ignoresIdentityAndOrderValue() {
            EventQuestion saved = saved(101L, QuestionType.SINGLE_CHOICE, List.of("참석", "불참"), false);
            EventQuestion requested = EventQuestion.create(
                15L, 5, command(QuestionType.SINGLE_CHOICE, List.of("참석", "불참"), false));

            assertTrue(saved.hasSameContentAs(requested));
        }

        @Test
        @DisplayName("유형이 다르면 다르다")
        void differentType() {
            assertFalse(saved(1L, QuestionType.SINGLE_CHOICE, List.of("참석"), false)
                .hasSameContentAs(saved(1L, QuestionType.MULTIPLE_CHOICE, List.of("참석"), false)));
        }

        @Test
        @DisplayName("선택지 순서가 바뀌면 다르다 — 답변이 선택지 순서로 저장되기 때문이다")
        void differentOptionOrder() {
            assertFalse(saved(1L, QuestionType.SINGLE_CHOICE, List.of("참석", "불참"), false)
                .hasSameContentAs(saved(1L, QuestionType.SINGLE_CHOICE, List.of("불참", "참석"), false)));
        }

        @Test
        @DisplayName("'기타' 여부가 다르면 다르다")
        void differentOtherOption() {
            assertFalse(saved(1L, QuestionType.SINGLE_CHOICE, List.of("참석"), false)
                .hasSameContentAs(saved(1L, QuestionType.SINGLE_CHOICE, List.of("참석"), true)));
        }

        @Test
        @DisplayName("질문 내용이나 필수 여부가 다르면 다르다")
        void differentTextOrRequired() {
            EventQuestion base = saved(1L, QuestionType.SHORT_TEXT, List.of(), false);

            assertFalse(base.hasSameContentAs(
                EventQuestion.of(1L, 15L, "전화번호", QuestionType.SHORT_TEXT, true, 1, List.of(), false)));
            assertFalse(base.hasSameContentAs(
                EventQuestion.of(1L, 15L, "참여 종목", QuestionType.SHORT_TEXT, false, 1, List.of(), false)));
        }
    }

    @Test
    @DisplayName("선택형 유형은 단일·복수 선택뿐이다")
    void choiceTypes() {
        assertTrue(QuestionType.SINGLE_CHOICE.isChoice());
        assertTrue(QuestionType.MULTIPLE_CHOICE.isChoice());
        assertFalse(QuestionType.SHORT_TEXT.isChoice());
        assertFalse(QuestionType.LONG_TEXT.isChoice());
    }
}
