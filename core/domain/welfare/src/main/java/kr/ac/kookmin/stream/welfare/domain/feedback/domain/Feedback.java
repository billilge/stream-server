package kr.ac.kookmin.stream.welfare.domain.feedback.domain;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Feedback {

    private Long id;
    private int year;
    private int round;
    private String question;
    private LocalDateTime questionedAt;
    private String answer;
    private LocalDateTime answeredAt;
    private Long createdBy;
    private Long answeredBy;

    /**
     * 회차는 서버가 자동 배정하므로 여기서 받는다(아직 답변 없음).
     *
     * @param questionedAt 질문 시각. 시계를 호출부에 둬서 회차 판정에 쓴 시각과 같은 값이 기록되게 한다
     */
    public static Feedback create(int year, int round, String question, Long createdBy, LocalDateTime questionedAt) {
        return new Feedback(null, year, round, question, questionedAt, null, null, createdBy, null);
    }

    // DB에서 복원할 때 모든 필드를 그대로 받는다.
    public static Feedback of(
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
        return new Feedback(id, year, round, question, questionedAt, answer, answeredAt, createdBy, answeredBy);
    }

    public boolean isAnswered() {
        return answer != null;
    }

    // 답변을 작성한다. 이미 답변이 있으면 덮어쓴다 — 답변 수정이 같은 경로로 들어오기 때문이다
    public void writeAnswer(String answer, Long answeredBy, LocalDateTime answeredAt) {
        this.answer = answer;
        this.answeredBy = answeredBy;
        this.answeredAt = answeredAt;
    }
}
