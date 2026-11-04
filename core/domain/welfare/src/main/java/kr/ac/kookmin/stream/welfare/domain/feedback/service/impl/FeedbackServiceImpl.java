package kr.ac.kookmin.stream.welfare.domain.feedback.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.DateUtil;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
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
    public Feedback getById(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
            .orElseThrow(() -> new BusinessException(FeedbackErrorCode.FEEDBACK_NOT_FOUND));
        return hideUnpublishedAnswer(feedback, findPublishedPeriods(feedback.getYear()));
    }

    @Override
    public PageResult<Feedback> search(FeedbackPeriod period, PageOffset pageOffset) {
        // 한 페이지에 여러 회차가 섞일 수 있어 공개 여부를 질문마다 조회하지 않고 미리 한 번에 구한다
        Set<FeedbackPeriod> publishedPeriods = findPublishedPeriods(period.year());
        return feedbackRepository.search(period.year(), period.round(), pageOffset)
            .map(feedback -> hideUnpublishedAnswer(feedback, publishedPeriods));
    }

    @Override
    @Transactional
    public Feedback create(Long memberId, String question) {
        // 시각을 한 번만 읽어 회차 판정과 질문 시각에 같은 값을 쓴다. 두 번 읽으면 자정 경계에서
        // "9월 회차로 판정했는데 질문 시각은 10월"이 될 수 있다
        LocalDateTime now = LocalDateTime.now(DateUtil.KST);

        FeedbackRound openRound = feedbackRoundRepository.findOpenAt(now)
            .orElseThrow(() -> new BusinessException(FeedbackErrorCode.FEEDBACK_NOT_OPEN));

        return feedbackRepository.save(
            Feedback.create(openRound.getYear(), openRound.getRound(), question, memberId, now));
    }

    @Override
    public List<Integer> getAvailableYears() {
        return feedbackRoundRepository.findDistinctYears();
    }

    @Override
    public List<Integer> getRoundsOf(int year) {
        return feedbackRoundRepository.findRoundsByYear(year);
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
