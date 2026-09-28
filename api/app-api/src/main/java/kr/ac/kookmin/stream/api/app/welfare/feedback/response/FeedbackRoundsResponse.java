package kr.ac.kookmin.stream.api.app.welfare.feedback.response;

import java.util.List;

public record FeedbackRoundsResponse(List<Integer> years, int year, List<Integer> rounds) {

    public static FeedbackRoundsResponse of(List<Integer> years, int year, List<Integer> rounds) {
        return new FeedbackRoundsResponse(years, year, rounds);
    }
}
