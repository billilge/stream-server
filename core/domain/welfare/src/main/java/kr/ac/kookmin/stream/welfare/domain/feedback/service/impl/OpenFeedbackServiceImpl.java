package kr.ac.kookmin.stream.welfare.domain.feedback.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackErrorCode;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRound;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRoundOptions;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.OpenFeedback;
import kr.ac.kookmin.stream.welfare.domain.feedback.repository.FeedbackRoundRepository;
import kr.ac.kookmin.stream.welfare.domain.feedback.repository.OpenFeedbackRepository;
import kr.ac.kookmin.stream.welfare.domain.feedback.service.OpenFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class OpenFeedbackServiceImpl implements OpenFeedbackService {

    private final OpenFeedbackRepository openFeedbackRepository;
    private final FeedbackRoundRepository feedbackRoundRepository;

    @Override
    @Transactional(readOnly = true)
    public OpenFeedback getById(Long id) {
        return openFeedbackRepository.findById(id)
            .orElseThrow(() -> new BusinessException(FeedbackErrorCode.FEEDBACK_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<OpenFeedback> search(Integer year, Integer round, PageOffset pageOffset) {
        if (round != null && round <= 0) {
            throw new BusinessException(FeedbackErrorCode.INVALID_FEEDBACK_ROUND);
        }
        return openFeedbackRepository.search(year, round, pageOffset);
    }

    @Override
    @Transactional
    public OpenFeedback create(Long memberId, String question) {
        FeedbackRound openRound = feedbackRoundRepository.findOpenAt(LocalDateTime.now())
            .orElseThrow(() -> new BusinessException(FeedbackErrorCode.FEEDBACK_NOT_OPEN));

        OpenFeedback feedback = OpenFeedback.create(openRound.year(), openRound.round(), question, memberId);
        return openFeedbackRepository.save(feedback);
    }

    @Override
    @Transactional(readOnly = true)
    public FeedbackRoundOptions getRoundOptions(Integer year) {
        List<Integer> years = feedbackRoundRepository.findDistinctYears();
        int targetYear = year != null ? year : LocalDateTime.now().getYear();
        List<Integer> rounds = feedbackRoundRepository.findRoundsByYear(targetYear);

        return FeedbackRoundOptions.of(years, targetYear, rounds);
    }
}
