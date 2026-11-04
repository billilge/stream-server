package kr.ac.kookmin.stream.welfare.domain.feedback.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.common.PreviewResult;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.Feedback;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackErrorCode;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackPeriod;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRound;
import kr.ac.kookmin.stream.welfare.domain.feedback.repository.FeedbackRepository;
import kr.ac.kookmin.stream.welfare.domain.feedback.repository.FeedbackRoundRepository;
import kr.ac.kookmin.stream.welfare.domain.feedback.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackRoundRepository feedbackRoundRepository;

    @Override
    public Feedback getById(Long id, Long memberId) {
        Feedback feedback = feedbackRepository.findById(id)
            .orElseThrow(() -> new BusinessException(FeedbackErrorCode.FEEDBACK_NOT_FOUND));
        Feedback visible = hideUnpublishedAnswer(feedback, findPublishedPeriods(feedback.getYear()));
        validateReadable(visible, memberId);
        return visible;
    }

    @Override
    public PageResult<Feedback> search(FeedbackPeriod period, String keyword, PageOffset pageOffset) {
        return feedbackRepository.searchPublished(period.year(), period.round(), keyword, pageOffset);
    }

    // 미리보기와 전체 수를 두 문장으로 조회한다. 저장소의 @Query 메서드에는 트랜잭션이 붙지 않으므로, 두 조회가 같은
    // 스냅샷을 보도록 읽기 트랜잭션으로 묶는다
    @Override
    @Transactional(readOnly = true)
    public PreviewResult<Feedback> searchPreview(String keyword, int limit) {
        return feedbackRepository.findPublishedSearchPreview(keyword, limit);
    }

    @Override
    public PageResult<Feedback> getMyFeedbacks(Long memberId, PageOffset pageOffset) {
        // 내 질문은 여러 연도에 걸칠 수 있어 전체 연도의 공개 회차를 구한다
        Set<FeedbackPeriod> publishedPeriods = findPublishedPeriods(null);
        return feedbackRepository.findAllByCreatedBy(memberId, pageOffset)
            .map(feedback -> hideUnpublishedAnswer(feedback, publishedPeriods));
    }

    @Override
    @Transactional
    public Feedback create(Long memberId, String question) {
        // 시각을 한 번만 읽어 회차 판정과 질문 시각에 같은 값을 쓴다. 두 번 읽으면 자정 경계에서
        // "9월 회차로 판정했는데 질문 시각은 10월"이 될 수 있다.
        // 컬럼(DATETIME)이 초까지만 저장하므로 초 단위로 끊는다. 그래야 등록 응답과 이후 조회 응답의 시각이 같고,
        // 마감 23:59:59 직후의 소수 초(23:59:59.5)도 접수 기간 안으로 판정된다
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        FeedbackRound openRound = feedbackRoundRepository.findOpenAt(now)
            .orElseThrow(() -> new BusinessException(FeedbackErrorCode.FEEDBACK_NOT_OPEN));

        return feedbackRepository.save(
            Feedback.create(openRound.getYear(), openRound.getRound(), question, memberId, now));
    }

    @Override
    public long countByMemberId(Long memberId) {
        return feedbackRepository.countByMemberId(memberId);
    }

    @Override
    public List<Integer> getAvailableYears() {
        return feedbackRoundRepository.findDistinctPublishedYears();
    }

    @Override
    public List<Integer> getRoundsOf(int year) {
        return feedbackRoundRepository.findPublishedRoundsByYear(year);
    }

    // 남의 질문은 공개된 답변이 있을 때만 보인다. 질문이 있다는 것도 드러내지 않게 없는 질문과 같은 오류를 낸다
    private void validateReadable(Feedback visible, Long memberId) {
        if (!visible.isAnswered() && !visible.isWrittenBy(memberId)) {
            throw new BusinessException(FeedbackErrorCode.FEEDBACK_NOT_FOUND);
        }
    }

    // 학생에게는 공개된 회차의 답변만 보인다. 공개 전 답변은 운영진이 임시저장한 것이다
    private static Feedback hideUnpublishedAnswer(Feedback feedback, Set<FeedbackPeriod> publishedPeriods) {
        if (publishedPeriods.contains(FeedbackPeriod.of(feedback.getYear(), feedback.getRound()))) {
            return feedback;
        }
        return feedback.withoutAnswer();
    }

    // year가 null이면 전체 연도. 회차는 연 5~10개라 연도 단위로 다 읽어도 부담이 없다
    private Set<FeedbackPeriod> findPublishedPeriods(Integer year) {
        return feedbackRoundRepository.findAllByYear(year).stream()
            .filter(FeedbackRound::isAnswersPublished)
            .map(round -> FeedbackPeriod.of(round.getYear(), round.getRound()))
            .collect(Collectors.toSet());
    }
}
