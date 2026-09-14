package kr.ac.kookmin.stream.api.app.event.event.response;

import java.util.List;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.QuestionType;

public record EventFormQuestionResponse(
    Long questionId,
    String questionText,
    QuestionType questionType,
    boolean isRequired,
    int displayOrder,
    List<String> options,
    Integer maxLength
) {

    public static EventFormQuestionResponse from(EventQuestion question) {
        QuestionType questionType = question.getQuestionType();
        return new EventFormQuestionResponse(
            question.getId(),
            question.getQuestionText(),
            questionType,
            question.isRequired(),
            question.getDisplayOrder(),
            question.getOptions() == null ? List.of() : question.getOptions(),
            questionType.maxLength()
        );
    }
}
