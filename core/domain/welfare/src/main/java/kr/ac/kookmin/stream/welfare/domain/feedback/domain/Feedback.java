package kr.ac.kookmin.stream.welfare.domain.feedback.domain;

import java.time.LocalDateTime;

public record OpenFeedback(
    Long id,
    int year,
    int round,
    String question,
    LocalDateTime questionedAt,
    String answer,
    LocalDateTime answeredAt,
    Long createdBy,
    Long answeredBy
) {

    // 회차는 서버가 자동 배정하므로 여기서 받는다(요청 시각 = questionedAt, 아직 답변 없음).
    public static OpenFeedback create(int year, int round, String question, Long createdBy) {
        return new OpenFeedback(null, year, round, question, LocalDateTime.now(), null, null, createdBy, null);
    }

    // DB에서 복원할 때 모든 필드를 그대로 받는다.
    public static OpenFeedback of(
        Long id,
        int year,
        int round,
        String question,
        LocalDateTime questionedAt,
        String answer,
        LocalDateTime answeredAt,
        Long createdBy,
        Long answeredBy
    ) {
        return new OpenFeedback(id, year, round, question, questionedAt, answer, answeredAt, createdBy, answeredBy);
    }
}
