package kr.ac.kookmin.stream.api.admin.welfare.feedback.response;

import java.time.LocalDateTime;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRound;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRoundStatus;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRoundSummary;

public record FeedbackRoundResponse(
    Long roundId,
    int year,
    int round,
    LocalDateTime opensAt,
    LocalDateTime closesAt,
    FeedbackRoundStatus status,
    long questionCount,
    long answerCount,
    LocalDateTime answersPublishedAt
) {

    public static FeedbackRoundResponse from(FeedbackRoundSummary summary) {
        FeedbackRound round = summary.round();

        return new FeedbackRoundResponse(
            round.getId(),
            round.getYear(),
            round.getRound(),
            round.getOpensAt(),
            round.getClosesAt(),
            summary.status(),
            summary.questionCount(),
            summary.answerCount(),
            round.getAnswersPublishedAt()
        );
    }
}
