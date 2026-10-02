package kr.ac.kookmin.stream.db.welfare;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import kr.ac.kookmin.stream.db.common.BaseTimeEntity;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRound;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "feedback_rounds",
    uniqueConstraints = @UniqueConstraint(name = "uk_feedback_rounds_year_round", columnNames = {"year", "round"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedbackRoundJpaEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_round_id")
    private Long id;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int round;

    @Column(name = "opens_at", nullable = false)
    private LocalDateTime opensAt;

    @Column(name = "closes_at", nullable = false)
    private LocalDateTime closesAt;

    private FeedbackRoundJpaEntity(FeedbackRound feedbackRound) {
        this.id = feedbackRound.id();
        this.year = feedbackRound.year();
        this.round = feedbackRound.round();
        this.opensAt = feedbackRound.opensAt();
        this.closesAt = feedbackRound.closesAt();
    }

    public static FeedbackRoundJpaEntity from(FeedbackRound feedbackRound) {
        return new FeedbackRoundJpaEntity(feedbackRound);
    }

    public FeedbackRound toDomain() {
        return FeedbackRound.of(id, year, round, opensAt, closesAt);
    }
}
