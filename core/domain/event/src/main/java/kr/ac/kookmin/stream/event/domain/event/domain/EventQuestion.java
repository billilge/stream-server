package kr.ac.kookmin.stream.event.domain.event.domain;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EventQuestion {

    private Long id;
    private Long eventId;
    private String questionText;
    private QuestionType questionType;
    private boolean required;
    private int displayOrder;
    private List<String> options;

    public static EventQuestion of(
        Long id,
        Long eventId,
        String questionText,
        QuestionType questionType,
        boolean required,
        int displayOrder,
        List<String> options
    ) {
        return new EventQuestion(id, eventId, questionText, questionType, required, displayOrder, options);
    }
}
