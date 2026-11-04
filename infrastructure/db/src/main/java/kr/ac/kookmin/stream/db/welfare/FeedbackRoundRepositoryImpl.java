package kr.ac.kookmin.stream.db.welfare;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.db.common.ConstraintViolationUtil;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackErrorCode;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRound;
import kr.ac.kookmin.stream.welfare.domain.feedback.repository.FeedbackRoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FeedbackRoundRepositoryImpl implements FeedbackRoundRepository {

    private static final String YEAR_ROUND_UNIQUE_CONSTRAINT = "uk_feedback_rounds_year_round";

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
    public Optional<FeedbackRound> findLatestStartedUnpublished(LocalDateTime now) {
        return feedbackRoundJpaRepository
            .findFirstByAnswersPublishedAtIsNullAndOpensAtLessThanEqualOrderByOpensAtDesc(now)
            .map(FeedbackRoundJpaEntity::toDomain);
    }

    @Override
    public Optional<FeedbackRound> findNearestUpcoming(LocalDateTime now) {
        return feedbackRoundJpaRepository
            .findFirstByAnswersPublishedAtIsNullAndOpensAtAfterOrderByOpensAtAsc(now)
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
        try {
            // 동시 등록으로 같은 회차 번호를 함께 배정받은 경우 유니크 위반을 이 자리에서 잡으려면 바로 반영해야 한다
            return feedbackRoundJpaRepository.saveAndFlush(FeedbackRoundJpaEntity.from(feedbackRound)).toDomain();
        } catch (DataIntegrityViolationException e) {
            if (ConstraintViolationUtil.isViolated(e, YEAR_ROUND_UNIQUE_CONSTRAINT)) {
                throw new BusinessException(FeedbackErrorCode.FEEDBACK_ROUND_ALREADY_EXISTS);
            }
            throw e;
        }
    }
}
