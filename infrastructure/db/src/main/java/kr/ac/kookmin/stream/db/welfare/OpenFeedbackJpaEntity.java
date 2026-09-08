package kr.ac.kookmin.stream.db.welfare;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import kr.ac.kookmin.stream.db.common.BaseSoftDeleteEntity;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.OpenFeedback;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "open_feedbacks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OpenFeedbackJpaEntity extends BaseSoftDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    private Long id;

    @Column(nullable = false)
    private int round;

    @Column(nullable = false, length = 20)
    private String category;

    @Column(nullable = false, length = 1000)
    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "answered_by")
    private Long answeredBy;

    private OpenFeedbackJpaEntity(OpenFeedback feedback) {
        this.id = feedback.getId();
        this.round = feedback.getRound();
        this.category = feedback.getCategory();
        this.question = feedback.getQuestion();
        this.answer = feedback.getAnswer();
        this.answeredAt = feedback.getAnsweredAt();
        this.createdBy = feedback.getCreatedBy();
        this.answeredBy = feedback.getAnsweredBy();
    }

    public static OpenFeedbackJpaEntity from(OpenFeedback feedback) {
        return new OpenFeedbackJpaEntity(feedback);
    }

    public OpenFeedback toDomain() {
        return OpenFeedback.of(id, round, category, question, answer, answeredAt, createdBy, answeredBy);
    }
}
