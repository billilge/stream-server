package kr.ac.kookmin.stream.welfare.domain.feedback.domain;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenFeedback {

    private Long id;
    private int round;
    private String category;
    private String question;
    private String answer;
    private LocalDateTime answeredAt;
    private Long createdBy;
    private Long answeredBy;

    public static OpenFeedback of(
        Long id,
        int round,
        String category,
        String question,
        String answer,
        LocalDateTime answeredAt,
        Long createdBy,
        Long answeredBy
    ) {
        return new OpenFeedback(id, round, category, question, answer, answeredAt, createdBy, answeredBy);
    }
}
