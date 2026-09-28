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
    private int year;

    @Column(nullable = false)
    private int round;

    @Column(nullable = false, length = 500)
    private String question;

    // BaseTimeEntity의 createdAt(DB DEFAULT CURRENT_TIMESTAMP, insertable=false)에 기대지 않고 직접 값을 채운다.
    // insertable=false 컬럼은 save() 직후엔 DB에서 다시 읽어오기 전까지 자바 객체에 null로 남아있어서,
    // 등록 응답에 질문 시각을 바로 내려줘야 하는 이 도메인엔 맞지 않는다.
    @Column(name = "questioned_at", nullable = false)
    private LocalDateTime questionedAt;

    @Column(columnDefinition = "TEXT")
    private String answer;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "answered_by")
    private Long answeredBy;

    private OpenFeedbackJpaEntity(OpenFeedback feedback) {
        this.id = feedback.id();
        this.year = feedback.year();
        this.round = feedback.round();
        this.question = feedback.question();
        this.questionedAt = feedback.questionedAt();
        this.answer = feedback.answer();
        this.answeredAt = feedback.answeredAt();
        this.createdBy = feedback.createdBy();
        this.answeredBy = feedback.answeredBy();
    }

    public static OpenFeedbackJpaEntity from(OpenFeedback feedback) {
        return new OpenFeedbackJpaEntity(feedback);
    }

    public OpenFeedback toDomain() {
        return OpenFeedback.of(id, year, round, question, questionedAt, answer, answeredAt, createdBy, answeredBy);
    }
}
