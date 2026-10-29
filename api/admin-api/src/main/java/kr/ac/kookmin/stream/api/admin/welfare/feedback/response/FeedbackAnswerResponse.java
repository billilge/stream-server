package kr.ac.kookmin.stream.api.admin.welfare.feedback.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.api.admin.welfare.feedback.usecase.MemberFeedback;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.Feedback;

// 회차 답변 보기 모달의 한 항목. 답변이 없는 질문도 함께 내려간다
public record FeedbackAnswerResponse(
    Long feedbackId,
    String question,
    String memberName,
    LocalDateTime questionedAt,
    String answer,
    LocalDateTime answeredAt
) {

    public static FeedbackAnswerResponse from(MemberFeedback memberFeedback) {
        Feedback feedback = memberFeedback.feedback();

        return new FeedbackAnswerResponse(
            feedback.getId(),
            feedback.getQuestion(),
            memberFeedback.memberName(),
            feedback.getQuestionedAt(),
            feedback.getAnswer(),
            feedback.getAnsweredAt()
        );
    }
}
