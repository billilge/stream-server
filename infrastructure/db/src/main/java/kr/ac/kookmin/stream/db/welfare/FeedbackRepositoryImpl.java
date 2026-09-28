package kr.ac.kookmin.stream.db.welfare;

import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.OpenFeedback;
import kr.ac.kookmin.stream.welfare.domain.feedback.repository.OpenFeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OpenFeedbackRepositoryImpl implements OpenFeedbackRepository {

    private final OpenFeedbackJpaRepository openFeedbackJpaRepository;

    @Override
    public Optional<OpenFeedback> findById(Long id) {
        return openFeedbackJpaRepository.findByIdAndIsDeletedFalse(id).map(OpenFeedbackJpaEntity::toDomain);
    }

    @Override
    public PageResult<OpenFeedback> search(Integer year, Integer round, PageOffset pageOffset) {
        Page<OpenFeedbackJpaEntity> result = openFeedbackJpaRepository.search(
            year, round, PageRequest.of(pageOffset.page(), pageOffset.size())
        );

        return PageResult.of(
            result.getContent().stream().map(OpenFeedbackJpaEntity::toDomain).toList(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements()
        );
    }

    @Override
    public OpenFeedback save(OpenFeedback feedback) {
        return openFeedbackJpaRepository.save(OpenFeedbackJpaEntity.from(feedback)).toDomain();
    }
}
