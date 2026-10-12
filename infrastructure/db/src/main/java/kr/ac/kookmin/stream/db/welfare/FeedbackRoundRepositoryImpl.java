package kr.ac.kookmin.stream.db.welfare;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRound;
import kr.ac.kookmin.stream.welfare.domain.feedback.repository.FeedbackRoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FeedbackRoundRepositoryImpl implements FeedbackRoundRepository {

    private final FeedbackRoundJpaRepository feedbackRoundJpaRepository;

    @Override
    public Optional<FeedbackRound> findOpenAt(LocalDateTime now) {
        return feedbackRoundJpaRepository.findOpenAt(now).map(FeedbackRoundJpaEntity::toDomain);
    }

    @Override
    public List<Integer> findDistinctYears() {
        return feedbackRoundJpaRepository.findDistinctYears();
    }

    @Override
    public List<Integer> findRoundsByYear(int year) {
        return feedbackRoundJpaRepository.findRoundsByYear(year);
    }
}
