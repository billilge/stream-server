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
    public Optional<FeedbackRound> findById(Long id) {
        return feedbackRoundJpaRepository.findById(id).map(FeedbackRoundJpaEntity::toDomain);
    }

    @Override
    public Optional<FeedbackRound> findOpenAt(LocalDateTime now) {
        return feedbackRoundJpaRepository.findOpenAt(now).map(FeedbackRoundJpaEntity::toDomain);
    }

    @Override
    public Optional<FeedbackRound> findLatestUnpublished() {
        return feedbackRoundJpaRepository.findFirstByAnswersPublishedAtIsNullOrderByYearDescRoundDesc()
            .map(FeedbackRoundJpaEntity::toDomain);
    }

    @Override
    public List<FeedbackRound> findAllByYear(Integer year) {
        return feedbackRoundJpaRepository.findAllByYear(year).stream()
            .map(FeedbackRoundJpaEntity::toDomain)
            .toList();
    }

    @Override
    public boolean existsOverlapping(LocalDateTime opensAt, LocalDateTime closesAt) {
        return feedbackRoundJpaRepository.existsOverlapping(opensAt, closesAt);
    }

    @Override
    public Optional<Integer> findMaxRoundOf(int year) {
        return Optional.ofNullable(feedbackRoundJpaRepository.findMaxRoundOf(year));
    }

    @Override
    public List<Integer> findDistinctYears() {
        return feedbackRoundJpaRepository.findDistinctYears();
    }

    @Override
    public List<Integer> findRoundsByYear(int year) {
        return feedbackRoundJpaRepository.findRoundsByYear(year);
    }

    @Override
    public FeedbackRound save(FeedbackRound feedbackRound) {
        return feedbackRoundJpaRepository.save(FeedbackRoundJpaEntity.from(feedbackRound)).toDomain();
    }
}
