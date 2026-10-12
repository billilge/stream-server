package kr.ac.kookmin.stream.welfare.domain.feedback.domain;

import java.time.LocalDateTime;

// 관리자가 여는 열린피드백 접수 회차. 오픈/마감 시각과 함께 연도별로 관리한다(연도별로 1차부터 다시 시작).
public record FeedbackRound(Long id, int year, int round, LocalDateTime opensAt, LocalDateTime closesAt) {

    public static FeedbackRound of(Long id, int year, int round, LocalDateTime opensAt, LocalDateTime closesAt) {
        return new FeedbackRound(id, year, round, opensAt, closesAt);
    }
}
