package kr.ac.kookmin.stream.db.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.List;
import kr.ac.kookmin.stream.db.common.BaseTimeEntity;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationAnswer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
    name = "event_application_answers",
    indexes = {
        @Index(name = "idx_event_application_answers_event_application_id", columnList = "event_application_id")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventApplicationAnswerJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_application_answer_id")
    private Long id;

    @Column(name = "event_application_id", nullable = false)
    private Long eventApplicationId;

    @Column(name = "event_question_id", nullable = false)
    private Long eventQuestionId;

    @Column(name = "answer_text", columnDefinition = "TEXT")
    private String answerText;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "selected_options", columnDefinition = "json")
    private List<Integer> selectedOptions;

    private EventApplicationAnswerJpaEntity(EventApplicationAnswer answer) {
        this.id = answer.getId();
        this.eventApplicationId = answer.getEventApplicationId();
        this.eventQuestionId = answer.getEventQuestionId();
        this.answerText = answer.getAnswerText();
        this.selectedOptions = answer.getSelectedOptions();
    }

    public static EventApplicationAnswerJpaEntity from(EventApplicationAnswer answer) {
        return new EventApplicationAnswerJpaEntity(answer);
    }

    public EventApplicationAnswer toDomain() {
        return EventApplicationAnswer.of(id, eventApplicationId, eventQuestionId, answerText, selectedOptions);
    }
}
