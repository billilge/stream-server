package kr.ac.kookmin.stream.welfare.domain.feedback.domain;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 회차 현황과 그 회차에 접수된 질문 전체(답변이 없는 질문도 포함).
 * 질문을 이미 다 읽은 뒤라 질문 수·답변 수는 집계 쿼리 없이 목록에서 센다.
 */
public record FeedbackRoundDetail(FeedbackRoundSummary summary, List<Feedback> feedbacks) {

    public static FeedbackRoundDetail of(FeedbackRound round, List<Feedback> feedbacks, LocalDateTime now) {
        long answerCount = feedbacks.stream().filter(Feedback::isAnswered).count();
        FeedbackCount count = FeedbackCount.of(round.year(), round.round(), feedbacks.size(), answerCount);

        return new FeedbackRoundDetail(FeedbackRoundSummary.of(round, count, now), feedbacks);
    }
}
