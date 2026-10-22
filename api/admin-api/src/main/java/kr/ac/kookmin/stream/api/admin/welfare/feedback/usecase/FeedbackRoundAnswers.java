package kr.ac.kookmin.stream.api.admin.welfare.feedback.usecase;

import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRoundSummary;

// 회차 현황과 그 회차의 질문·답변 전체에 작성자를 붙인 결과.
public record FeedbackRoundAnswers(FeedbackRoundSummary summary, List<FeedbackWithStudent> feedbacks) {
}
