package kr.ac.kookmin.stream.event.domain.event.domain;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EventApplicationAnswer {

    private Long id;
    private Long eventApplicationId;
    private Long eventQuestionId;
    private String answerText;
    private List<Integer> selectedOptions;

    public static EventApplicationAnswer of(
        Long id,
        Long eventApplicationId,
        Long eventQuestionId,
        String answerText,
        List<Integer> selectedOptions
    ) {
        return new EventApplicationAnswer(id, eventApplicationId, eventQuestionId, answerText, selectedOptions);
    }
}
