package kr.ac.kookmin.stream.welfare.domain.feedback.domain;

import java.util.List;

// 목록 화면의 연도 드롭다운·회차 칩을 그리는 데 쓰는 조회 전용 값 객체.
// years: 피드백 회차가 하나라도 존재했던 전체 연도. rounds: year 기준으로 실제 존재하는 회차 번호.
public record FeedbackRoundOptions(List<Integer> years, int year, List<Integer> rounds) {

    public static FeedbackRoundOptions of(List<Integer> years, int year, List<Integer> rounds) {
        return new FeedbackRoundOptions(years, year, rounds);
    }
}
