package kr.ac.kookmin.stream.api.app.welfare.feedback.response;

import java.util.List;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRoundOptions;

public record FeedbackRoundsResponse(List<Integer> years, int year, List<Integer> rounds) {

    public static FeedbackRoundsResponse from(FeedbackRoundOptions options) {
        return new FeedbackRoundsResponse(options.years(), options.year(), options.rounds());
    }
}
