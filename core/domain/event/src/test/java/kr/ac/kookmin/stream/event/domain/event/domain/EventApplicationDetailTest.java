package kr.ac.kookmin.stream.event.domain.event.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EventApplicationDetailTest {

    private static final LocalDateTime APPLIED_AT = LocalDateTime.of(2026, 8, 30, 14, 3, 21);

    private static final EventQuestion CHOICE_QUESTION = EventQuestion.of(
        1L, 15L, "관심 분야를 선택해 주세요.", QuestionType.MULTIPLE_CHOICE, true, 0,
        List.of("텍스트1", "텍스트2", "텍스트3"));
    private static final EventQuestion TEXT_QUESTION = EventQuestion.of(
        2L, 15L, "궁금한 점을 남겨 주세요.", QuestionType.LONG_TEXT, false, 1, List.of());

    private static Event event() {
        return Event.of(
            15L, "동문패널톡", "설명", "전교생", "미래관 419호",
            APPLIED_AT.plusDays(30), APPLIED_AT.plusDays(31), APPLIED_AT.minusDays(7), APPLIED_AT.plusDays(3),
            RecruitType.OPEN, List.of(7L), 0, RecruitStatus.OPEN, true, 100L
        );
    }

    private static EventApplication application() {
        return EventApplication.of(102L, 15L, 1L, EventApplicationStatus.APPLIED, APPLIED_AT, null);
    }

    @Test
    @DisplayName("행사·신청 정보를 그대로 싣는다")
    void copiesEventAndApplication() {
        EventApplicationDetail detail = EventApplicationDetail.of(
            event(), application(), List.of(), List.of());

        assertEquals(15L, detail.eventId());
        assertEquals("동문패널톡", detail.title());
        assertEquals("미래관 419호", detail.place());
        assertEquals(EventApplicationStatus.APPLIED, detail.applicationStatus());
        assertEquals(APPLIED_AT, detail.appliedAt());
        assertNull(detail.canceledAt());
    }

    @Test
    @DisplayName("질문에 해당 신청의 답변을 짝지어 넣는다")
    void pairsAnswersWithQuestions() {
        List<EventApplicationAnswer> answers = List.of(
            EventApplicationAnswer.of(1L, 102L, 1L, null, List.of(0, 1)),
            EventApplicationAnswer.of(2L, 102L, 2L, "취업이 궁금합니다.", List.of())
        );

        EventApplicationDetail detail = EventApplicationDetail.of(
            event(), application(), List.of(CHOICE_QUESTION, TEXT_QUESTION), answers);

        assertEquals(List.of(0, 1), detail.questions().get(0).selectedOptions());
        assertNull(detail.questions().get(0).answerText());
        assertEquals("취업이 궁금합니다.", detail.questions().get(1).answerText());
        assertEquals(List.of(), detail.questions().get(1).selectedOptions());
    }

    @Test
    @DisplayName("질문 순서는 넘겨받은 순서를 그대로 지킨다")
    void keepsQuestionOrder() {
        EventApplicationDetail detail = EventApplicationDetail.of(
            event(), application(), List.of(CHOICE_QUESTION, TEXT_QUESTION), List.of());

        assertEquals(
            List.of(1L, 2L),
            detail.questions().stream().map(question -> question.question().getId()).toList());
    }

    @Test
    @DisplayName("답하지 않은 선택 질문도 빈 답변으로 함께 내려간다")
    void keepsUnansweredQuestions() {
        List<EventApplicationAnswer> answers = List.of(
            EventApplicationAnswer.of(1L, 102L, 1L, null, List.of(0)));

        EventApplicationDetail detail = EventApplicationDetail.of(
            event(), application(), List.of(CHOICE_QUESTION, TEXT_QUESTION), answers);

        assertEquals(2, detail.questions().size());
        assertNull(detail.questions().get(1).answerText());
        assertTrue(detail.questions().get(1).selectedOptions().isEmpty());
    }

    @Test
    @DisplayName("선택지가 null로 저장된 답변도 빈 배열로 내려간다")
    void nullSelectedOptionsBecomeEmpty() {
        List<EventApplicationAnswer> answers = List.of(
            EventApplicationAnswer.of(2L, 102L, 2L, "답변", null));

        EventApplicationDetail detail = EventApplicationDetail.of(
            event(), application(), List.of(TEXT_QUESTION), answers);

        assertEquals(List.of(), detail.questions().getFirst().selectedOptions());
    }
}
