package kr.ac.kookmin.stream.welfare.domain.feedback.service.impl;

import java.time.LocalDateTime;
import java.util.List;
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
}
