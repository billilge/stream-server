package kr.ac.kookmin.stream.api.app.event.event.response;

import java.util.List;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.QuestionType;

/**
 * @param hasOtherOption 선택형 질문에 '기타'(직접 입력) 선택지를 덧붙이는지. '기타' 문구는 신청할 때 answerText에 담는다
 * @param maxLength      answerText 길이 한도. 텍스트형은 답변 한도, '기타'를 허용한 선택형은 '기타' 문구 한도이고, 그 외
 *                       선택형은 {@code null}
 */
public record EventFormQuestionResponse(
    Long questionId,
    String questionText,
    QuestionType questionType,
    boolean isRequired,
    int displayOrder,
    List<String> options,
    boolean hasOtherOption,
    Integer maxLength
) {

    public static EventFormQuestionResponse from(EventQuestion question) {
        return new EventFormQuestionResponse(
            question.getId(),
            question.getQuestionText(),
            question.getQuestionType(),
            question.isRequired(),
            question.getDisplayOrder(),
            question.getOptions(),
            question.isOtherOptionAllowed(),
            question.answerTextMaxLength()
        );
    }
}
