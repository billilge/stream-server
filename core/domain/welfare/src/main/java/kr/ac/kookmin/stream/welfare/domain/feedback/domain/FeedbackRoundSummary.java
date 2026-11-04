package kr.ac.kookmin.stream.welfare.domain.feedback.domain;

import java.time.LocalDateTime;

// 회차 한 건의 현황 — 기간·상태·질문 수·답변 수. 관리자 요약 화면과 회차 목록이 같은 모양을 쓴다.
public record FeedbackRoundSummary(
    FeedbackRound round,
    FeedbackRoundStatus status,
    long questionCount,
    long answerCount
) {

    public static FeedbackRoundSummary of(FeedbackRound round, FeedbackCount count, LocalDateTime now) {
        return new FeedbackRoundSummary(
            round, FeedbackRoundStatus.of(round, now), count.questionCount(), count.answerCount());
    }
}
