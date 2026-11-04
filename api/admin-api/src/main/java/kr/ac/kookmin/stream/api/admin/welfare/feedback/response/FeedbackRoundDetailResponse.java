package kr.ac.kookmin.stream.api.admin.welfare.feedback.response;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.api.admin.welfare.feedback.usecase.FeedbackRoundAnswers;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRound;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRoundStatus;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRoundSummary;

// 회차 현황 + 그 회차의 질문·답변 전체. 답변 저장·공개 응답도 같은 모양이라 화면이 목록을 그대로 갱신한다
public record FeedbackRoundDetailResponse(
    Long roundId,
    int year,
    int round,
    LocalDateTime opensAt,
    LocalDateTime closesAt,
    FeedbackRoundStatus status,
    long questionCount,
    long answerCount,
    LocalDateTime answersPublishedAt,
    List<FeedbackAnswerResponse> feedbacks
) {

    public static FeedbackRoundDetailResponse from(FeedbackRoundAnswers roundAnswers) {
        FeedbackRoundSummary summary = roundAnswers.summary();
        FeedbackRound round = summary.round();

        return new FeedbackRoundDetailResponse(
            round.getId(),
            round.getYear(),
            round.getRound(),
            round.getOpensAt(),
            round.getClosesAt(),
            summary.status(),
            summary.questionCount(),
            summary.answerCount(),
            round.getAnswersPublishedAt(),
            roundAnswers.feedbacks().stream().map(FeedbackAnswerResponse::from).toList()
        );
    }
}
