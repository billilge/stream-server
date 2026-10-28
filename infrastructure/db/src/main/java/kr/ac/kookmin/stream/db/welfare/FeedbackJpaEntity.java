package kr.ac.kookmin.stream.db.welfare;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import kr.ac.kookmin.stream.db.common.BaseSoftDeleteEntity;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.Feedback;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

// 바뀐 컬럼만 UPDATE한다(@DynamicUpdate). 도메인 Feedback에는 삭제 여부가 없어 from()으로 만든 엔티티의 isDeleted는 늘 false라,
// 답변 저장이 질문 삭제와 겹치면 모든 컬럼을 되쓰면서 삭제 표시(is_deleted)를 되돌릴 수 있다. 답변 컬럼만 쓰게 해 이를 막는다.
// 동시 수정을 막는 락은 두지 않아 같은 질문의 답변 저장이 겹치면 나중에 저장한 내용이 반영된다.
@Entity
@Table(name = "open_feedbacks")
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedbackJpaEntity extends BaseSoftDeleteEntity {

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

    private FeedbackJpaEntity(Feedback feedback) {
        this.id = feedback.getId();
        this.year = feedback.getYear();
        this.round = feedback.getRound();
        this.question = feedback.getQuestion();
        this.questionedAt = feedback.getQuestionedAt();
        this.answer = feedback.getAnswer();
        this.answeredAt = feedback.getAnsweredAt();
        this.createdBy = feedback.getCreatedBy();
        this.answeredBy = feedback.getAnsweredBy();
    }

    public static FeedbackJpaEntity from(Feedback feedback) {
        return new FeedbackJpaEntity(feedback);
    }

    public Feedback toDomain() {
        return Feedback.of(id, year, round, question, questionedAt, answer, answeredAt, createdBy, answeredBy);
    }
}
