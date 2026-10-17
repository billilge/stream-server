package kr.ac.kookmin.stream.db.welfare;

import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.Feedback;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackCount;
import kr.ac.kookmin.stream.welfare.domain.feedback.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FeedbackRepositoryImpl implements FeedbackRepository {

    // IN에 빈 목록을 넘기면 JPQL이 깨지므로, 어떤 회원 ID와도 겹치지 않는 값을 넣어 "일치 없음"을 표현한다
    private static final List<Long> NO_MEMBER_IDS = List.of(0L);

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

        return toPageResult(result);
    }

    @Override
    public List<Feedback> findAllByYearAndRound(int year, int round) {
        return feedbackJpaRepository.findAllByYearAndRound(year, round).stream()
            .map(FeedbackJpaEntity::toDomain)
            .toList();
    }

    @Override
    public PageResult<Feedback> searchByKeyword(
        int year, int round, String keyword, List<Long> memberIds, PageOffset pageOffset
    ) {
        Page<FeedbackJpaEntity> result = feedbackJpaRepository.searchByKeyword(
            year,
            round,
            keyword,
            memberIds.isEmpty() ? NO_MEMBER_IDS : memberIds,
            PageRequest.of(pageOffset.page(), pageOffset.size())
        );

        return toPageResult(result);
    }

    @Override
    public List<FeedbackCount> findCountsByYear(Integer year) {
        return feedbackJpaRepository.findCountsByYear(year).stream()
            .map(row -> FeedbackCount.of(
                row.getYear(), row.getRound(), row.getQuestionCount(), row.getAnswerCount()))
            .toList();
    }

    @Override
    public Feedback save(Feedback feedback) {
        return feedbackJpaRepository.save(FeedbackJpaEntity.from(feedback)).toDomain();
    }

    @Override
    public List<Feedback> saveAll(List<Feedback> feedbacks) {
        return feedbackJpaRepository.saveAll(feedbacks.stream().map(FeedbackJpaEntity::from).toList()).stream()
            .map(FeedbackJpaEntity::toDomain)
            .toList();
    }

    private static PageResult<Feedback> toPageResult(Page<FeedbackJpaEntity> result) {
        return PageResult.of(
            result.getContent().stream().map(FeedbackJpaEntity::toDomain).toList(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements()
        );
    }
}
