package kr.ac.kookmin.stream.welfare.domain.feedback.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.Feedback;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackErrorCode;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRound;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRoundOptions;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackPeriod;
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
        return feedbackRepository.findById(id)
            .orElseThrow(() -> new BusinessException(FeedbackErrorCode.FEEDBACK_NOT_FOUND));
    }

    @Override
    public PageResult<Feedback> search(FeedbackPeriod period, PageOffset pageOffset) {
        return feedbackRepository.search(period.year(), period.round(), pageOffset);
    }

    @Override
    @Transactional
    public Feedback create(Long memberId, String question) {
        FeedbackRound openRound = feedbackRoundRepository.findOpenAt(LocalDateTime.now())
            .orElseThrow(() -> new BusinessException(FeedbackErrorCode.FEEDBACK_NOT_OPEN));

        Feedback feedback = Feedback.create(openRound.year(), openRound.round(), question, memberId);
        return feedbackRepository.save(feedback);
    }

    @Override
    public FeedbackRoundOptions getRoundOptions(Integer year) {
        List<Integer> years = feedbackRoundRepository.findDistinctYears();
        int targetYear = year != null ? year : LocalDateTime.now().getYear();
        List<Integer> rounds = feedbackRoundRepository.findRoundsByYear(targetYear);

        return FeedbackRoundOptions.of(years, targetYear, rounds);
    }
}
