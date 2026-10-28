package kr.ac.kookmin.stream.api.admin.welfare.feedback.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.api.admin.welfare.feedback.usecase.FeedbackWithStudent;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.Feedback;

// 현재 회차 질문 탭의 한 줄. studentName은 탈퇴(소프트 삭제)한 회원의 질문이면 null이다
public record AdminFeedbackResponse(
    Long feedbackId,
    int year,
    int round,
    String question,
    String studentName,
    LocalDateTime questionedAt,
    String answer,
    LocalDateTime answeredAt
) {

    public static AdminFeedbackResponse from(FeedbackWithStudent feedbackWithStudent) {
        Feedback feedback = feedbackWithStudent.feedback();
        Member student = feedbackWithStudent.student();

        return new AdminFeedbackResponse(
            feedback.getId(),
            feedback.getYear(),
            feedback.getRound(),
            feedback.getQuestion(),
            student == null ? null : student.getName(),
            feedback.getQuestionedAt(),
            feedback.getAnswer(),
            feedback.getAnsweredAt()
        );
    }
}
