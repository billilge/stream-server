package kr.ac.kookmin.stream.api.app.welfare.feedback.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.Feedback;

public record FeedbackResponse(
    Long feedbackId,
    int year,
    int round,
    String question,
    LocalDateTime questionedAt,
    LocalDateTime answeredAt,
    String answer
) {
    public static FeedbackResponse from(Feedback feedback) {
        return new FeedbackResponse(
            feedback.id(),
            feedback.year(),
            feedback.round(),
            feedback.question(),
            feedback.questionedAt(),
            feedback.answeredAt(),
            feedback.answer()
        );
    }
}
