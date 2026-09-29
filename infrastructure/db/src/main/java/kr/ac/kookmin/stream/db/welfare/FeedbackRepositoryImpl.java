package kr.ac.kookmin.stream.db.welfare;

import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.Feedback;
import kr.ac.kookmin.stream.welfare.domain.feedback.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FeedbackRepositoryImpl implements FeedbackRepository {

    private final FeedbackJpaRepository feedbackJpaRepository;

    @Override
    public Optional<Feedback> findById(Long id) {
        return feedbackJpaRepository.findByIdAndIsDeletedFalse(id).map(FeedbackJpaEntity::toDomain);
    }

    @Override
    public PageResult<Feedback> search(Integer year, Integer round, PageOffset pageOffset) {
        Page<FeedbackJpaEntity> result = feedbackJpaRepository.search(
            year, round, PageRequest.of(pageOffset.page(), pageOffset.size())
        );

        return PageResult.of(
            result.getContent().stream().map(FeedbackJpaEntity::toDomain).toList(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements()
        );
    }

    @Override
    public Feedback save(Feedback feedback) {
        return feedbackJpaRepository.save(FeedbackJpaEntity.from(feedback)).toDomain();
    }
}
