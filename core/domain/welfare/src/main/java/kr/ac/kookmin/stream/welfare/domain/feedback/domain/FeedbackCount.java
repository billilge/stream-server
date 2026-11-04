package kr.ac.kookmin.stream.welfare.domain.feedback.domain;

// 한 회차에 접수된 질문 수와 그중 답변이 작성된 수. 임시저장한 답변도 답변 수에 포함된다.
public record FeedbackCount(int year, int round, long questionCount, long answerCount) {

    public static FeedbackCount of(int year, int round, long questionCount, long answerCount) {
        return new FeedbackCount(year, round, questionCount, answerCount);
    }

    // 질문이 한 건도 없는 회차는 집계 쿼리의 GROUP BY 결과에 아예 나오지 않으므로 0으로 채운다.
    public static FeedbackCount empty(int year, int round) {
        return new FeedbackCount(year, round, 0, 0);
    }
}
