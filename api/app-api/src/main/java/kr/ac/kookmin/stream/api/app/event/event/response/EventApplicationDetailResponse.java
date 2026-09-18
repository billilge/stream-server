package kr.ac.kookmin.stream.api.app.event.event.response;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.QuestionType;

public record EventApplicationDetailResponse(
    Long eventId,
    String title,
    String place,
    EventApplicationStatus applicationStatus,
    LocalDateTime eventStartAt,
    LocalDateTime appliedAt,
    LocalDateTime canceledAt,
    List<AnsweredQuestion> questions
) {

    public static EventApplicationDetailResponse from(EventApplicationDetail detail) {
        return new EventApplicationDetailResponse(
            detail.eventId(),
            detail.title(),
            detail.place(),
            detail.applicationStatus(),
            detail.eventStartAt(),
            detail.appliedAt(),
            detail.canceledAt(),
            detail.questions().stream().map(AnsweredQuestion::from).toList()
        );
    }

    /**
     * 질문 한 건과 그 신청에서 제출한 답변.
     * <p>
     * 신청서 폼 조회({@link EventFormQuestionResponse})와 달리 답변 길이 제한(maxLength) 대신 제출한 답변을 싣는다.
     * 답하지 않은 선택 질문은 {@code answerText}가 {@code null}, {@code selectedOptions}가 빈 배열이다.
     */
    public record AnsweredQuestion(
        Long questionId,
        String questionText,
        QuestionType questionType,
        boolean isRequired,
        int displayOrder,
        List<String> options,
        String answerText,
        List<Integer> selectedOptions
    ) {

        public static AnsweredQuestion from(EventApplicationDetail.AnsweredQuestion answeredQuestion) {
            EventQuestion question = answeredQuestion.question();
            return new AnsweredQuestion(
                question.getId(),
                question.getQuestionText(),
                question.getQuestionType(),
                question.isRequired(),
                question.getDisplayOrder(),
                question.getOptions() == null ? List.of() : question.getOptions(),
                answeredQuestion.answerText(),
                answeredQuestion.selectedOptions()
            );
        }
    }
}
