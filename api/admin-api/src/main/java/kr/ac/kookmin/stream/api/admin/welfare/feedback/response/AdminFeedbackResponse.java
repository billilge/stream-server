package kr.ac.kookmin.stream.api.admin.welfare.feedback.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.api.admin.welfare.feedback.usecase.MemberFeedback;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.Feedback;

// 현재 회차 질문 탭의 한 줄. memberName은 탈퇴(소프트 삭제)한 회원의 질문이면 null이다
public record AdminFeedbackResponse(
    Long feedbackId,
    int year,
    int round,
    String question,
    String memberName,
    LocalDateTime questionedAt,
    String answer,
    LocalDateTime answeredAt
) {

    public static AdminFeedbackResponse from(MemberFeedback memberFeedback) {
        Feedback feedback = memberFeedback.feedback();

        return new AdminFeedbackResponse(
            feedback.getId(),
            feedback.getYear(),
            feedback.getRound(),
            feedback.getQuestion(),
            memberFeedback.memberName(),
            feedback.getQuestionedAt(),
            feedback.getAnswer(),
            feedback.getAnsweredAt()
        );
    }
}
