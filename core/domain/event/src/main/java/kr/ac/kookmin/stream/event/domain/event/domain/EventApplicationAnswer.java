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

    /**
     * DB에 저장된 값으로 답변을 복원한다. 고른 선택지 목록이 없으면 빈 목록으로 바꿔 둔다. 도메인 안에서는 목록이 항상 있다고
     * 보고 null을 확인하지 않는다.
     */
    public static EventApplicationAnswer of(
        Long id,
        Long eventApplicationId,
        Long eventQuestionId,
        String answerText,
        List<Integer> selectedOptions
    ) {
        return new EventApplicationAnswer(
            id, eventApplicationId, eventQuestionId, answerText, selectedOptions == null ? List.of() : selectedOptions
        );
    }

    /**
     * 새 답변을 만든다. 식별자는 저장 시 부여된다.
     */
    public static EventApplicationAnswer create(
        Long eventApplicationId,
        Long eventQuestionId,
        String answerText,
        List<Integer> selectedOptions
    ) {
        return new EventApplicationAnswer(null, eventApplicationId, eventQuestionId, answerText, selectedOptions);
    }
}
