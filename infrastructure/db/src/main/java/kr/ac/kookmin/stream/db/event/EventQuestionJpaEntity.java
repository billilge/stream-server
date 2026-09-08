package kr.ac.kookmin.stream.db.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.List;
import kr.ac.kookmin.stream.db.common.BaseTimeEntity;
import kr.ac.kookmin.stream.event.domain.event.domain.EventQuestion;
import kr.ac.kookmin.stream.event.domain.event.domain.QuestionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
    name = "event_questions",
    indexes = {
        @Index(name = "idx_event_questions_event_id", columnList = "event_id")
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventQuestionJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_question_id")
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "question_text", nullable = false, length = 500)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false, length = 30)
    private QuestionType questionType;

    @Column(name = "is_required", nullable = false)
    private boolean required;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> options;

    private EventQuestionJpaEntity(EventQuestion question) {
        this.id = question.getId();
        this.eventId = question.getEventId();
        this.questionText = question.getQuestionText();
        this.questionType = question.getQuestionType();
        this.required = question.isRequired();
        this.displayOrder = question.getDisplayOrder();
        this.options = question.getOptions();
    }

    public static EventQuestionJpaEntity from(EventQuestion question) {
        return new EventQuestionJpaEntity(question);
    }

    public EventQuestion toDomain() {
        return EventQuestion.of(id, eventId, questionText, questionType, required, displayOrder, options);
    }
}
