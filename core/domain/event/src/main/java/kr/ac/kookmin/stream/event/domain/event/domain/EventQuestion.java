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
    /** 선택형 질문에 '기타'(직접 입력) 선택지를 덧붙이는지. 학생 앱 노출·답변 저장은 아직 없다(후속 작업) */
    private boolean otherOptionAllowed;

    public static EventQuestion of(
        Long id,
        Long eventId,
        String questionText,
        QuestionType questionType,
        boolean required,
        int displayOrder,
        List<String> options,
        boolean otherOptionAllowed
    ) {
        return new EventQuestion(
            id, eventId, questionText, questionType, required, displayOrder, options, otherOptionAllowed
        );
    }

    /**
     * 새 질문을 만든다. 텍스트형 질문에 딸려 온 선택지·'기타' 여부는 의미가 없으므로 버린다.
     *
     * @param displayOrder 신청서에서 몇 번째로 보일지. 1부터 센다
     */
    public static EventQuestion create(Long eventId, int displayOrder, EventQuestionCommand command) {
        boolean choice = command.questionType().isChoice();
        return new EventQuestion(
            null,
            eventId,
            command.questionText(),
            command.questionType(),
            command.required(),
            displayOrder,
            choice ? List.copyOf(command.options()) : List.of(),
            choice && command.otherOptionAllowed()
        );
    }
}
