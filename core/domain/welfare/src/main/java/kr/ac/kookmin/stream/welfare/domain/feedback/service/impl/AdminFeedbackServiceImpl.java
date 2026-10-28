package kr.ac.kookmin.stream.welfare.domain.feedback.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.DateUtil;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.Feedback;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackAnswerCommand;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackCount;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackErrorCode;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackPeriod;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRound;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRoundDetail;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRoundStatus;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRoundSummary;
import kr.ac.kookmin.stream.welfare.domain.feedback.repository.FeedbackRepository;
import kr.ac.kookmin.stream.welfare.domain.feedback.repository.FeedbackRoundRepository;
import kr.ac.kookmin.stream.welfare.domain.feedback.service.AdminFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class AdminFeedbackServiceImpl implements AdminFeedbackService {

    private static final int FIRST_ROUND = 1;

    private final FeedbackRepository feedbackRepository;
    private final FeedbackRoundRepository feedbackRoundRepository;

    @Override
    @Transactional
    public FeedbackRoundSummary createRound(LocalDate opensOn, LocalDate closesOn) {
        LocalDateTime now = LocalDateTime.now(DateUtil.KST);
        int nextRound = feedbackRoundRepository.findMaxRoundOf(opensOn.getYear())
            .map(maxRound -> maxRound + 1)
            .orElse(FIRST_ROUND);
        FeedbackRound round = FeedbackRound.create(nextRound, opensOn, closesOn);

        // 학생 앱의 질문 등록은 접수 기간인 회차 "하나"를 찾아 배정하므로, 기간이 겹치면 질문 등록이 깨진다.
        // 범위 겹침은 유니크 제약으로 막을 수 없어 등록 전에 확인한다(조회-후-삽입이라 동시 등록 구멍은 남는다)
        if (feedbackRoundRepository.existsOverlapping(round.getOpensAt(), round.getClosesAt())) {
            throw new BusinessException(FeedbackErrorCode.FEEDBACK_ROUND_OVERLAPPED);
        }

        FeedbackRound saved = feedbackRoundRepository.save(round);
        return FeedbackRoundSummary.of(saved, FeedbackCount.empty(saved.getYear(), saved.getRound()), now);
    }

    @Override
    public Optional<FeedbackRoundSummary> getCurrentRound() {
        LocalDateTime now = LocalDateTime.now(DateUtil.KST);
        return feedbackRoundRepository.findLatestUnpublished()
            .map(round -> FeedbackRoundSummary.of(round, getCountOf(round), now));
    }

    /**
     * 상태는 저장하지 않는 파생값이라 SQL의 WHERE에 걸 수 없고, 따라서 LIMIT도 걸 수 없다.
     * 연도까지만 DB로 좁히고 상태 필터와 페이징은 메모리에서 한다 — 회차는 연 5~10개라 전체 연도를
     * 다 읽어도 수십 건이다. 회차가 수백 개로 늘면 상태를 질의할 수 있는 형태로 바꾸는 쪽을 다시 보자.
     */
    @Override
    public PageResult<FeedbackRoundSummary> getRounds(
        Integer year, FeedbackRoundStatus status, PageOffset pageOffset
    ) {
        LocalDateTime now = LocalDateTime.now(DateUtil.KST);
        Map<FeedbackPeriod, FeedbackCount> counts = getCountMapOf(year);

        List<FeedbackRoundSummary> summaries = feedbackRoundRepository.findAllByYear(year).stream()
            .map(round -> FeedbackRoundSummary.of(round, getCountOf(counts, round), now))
            .filter(summary -> status == null || summary.status() == status)
            .toList();

        return paginate(summaries, pageOffset);
    }

    @Override
    public FeedbackRoundDetail getRoundDetail(Long roundId) {
        return getDetailOf(getRoundById(roundId), LocalDateTime.now(DateUtil.KST));
    }

    @Override
    public PageResult<Feedback> searchQuestions(
        Long roundId, String keyword, List<Long> memberIds, PageOffset pageOffset
    ) {
        Optional<FeedbackRound> round = roundId == null
            ? feedbackRoundRepository.findLatestUnpublished()
            : Optional.of(getRoundById(roundId));

        // 진행 중 회차가 없는데 회차를 지정하지도 않았으면 보여줄 질문이 없다
        return round
            .map(target -> feedbackRepository.searchByKeyword(
                target.getYear(), target.getRound(), keyword, memberIds, pageOffset))
            .orElseGet(() -> PageResult.empty(pageOffset.page(), pageOffset.size()));
    }

    @Override
    @Transactional
    public FeedbackRoundDetail saveAnswers(Long roundId, List<FeedbackAnswerCommand> commands, Long answeredBy) {
        LocalDateTime now = LocalDateTime.now(DateUtil.KST);
        FeedbackRound round = getRoundById(roundId);

        Map<Long, Feedback> roundFeedbacks = feedbackRepository
            .findAllByYearAndRound(round.getYear(), round.getRound()).stream()
            .collect(Collectors.toMap(Feedback::getId, Function.identity()));

        // 보낸 것만 반영한다 — commands에 없는 질문의 기존 답변은 건드리지 않는다
        List<Feedback> answered = commands.stream()
            .map(command -> answer(roundFeedbacks, command, answeredBy, now))
            .toList();
        feedbackRepository.saveAll(answered);

        return getDetailOf(round, now);
    }

    @Override
    @Transactional
    public FeedbackRoundDetail publishAnswers(Long roundId) {
        LocalDateTime now = LocalDateTime.now(DateUtil.KST);
        FeedbackRound round = getRoundById(roundId);
        List<Feedback> feedbacks = feedbackRepository.findAllByYearAndRound(round.getYear(), round.getRound());

        if (feedbacks.stream().noneMatch(Feedback::isAnswered)) {
            throw new BusinessException(FeedbackErrorCode.FEEDBACK_ANSWER_NOT_FOUND);
        }

        round.publishAnswers(now);
        FeedbackRound published = feedbackRoundRepository.save(round);
        return FeedbackRoundDetail.of(published, feedbacks, now);
    }

    private FeedbackRound getRoundById(Long roundId) {
        return feedbackRoundRepository.findById(roundId)
            .orElseThrow(() -> new BusinessException(FeedbackErrorCode.FEEDBACK_ROUND_NOT_FOUND));
    }

    private FeedbackRoundDetail getDetailOf(FeedbackRound round, LocalDateTime now) {
        return FeedbackRoundDetail.of(
            round, feedbackRepository.findAllByYearAndRound(round.getYear(), round.getRound()), now);
    }

    private Feedback answer(
        Map<Long, Feedback> roundFeedbacks, FeedbackAnswerCommand command, Long answeredBy, LocalDateTime now
    ) {
        Feedback feedback = roundFeedbacks.get(command.feedbackId());
        if (feedback == null) {
            // 다른 회차의 질문인지, 아예 없는 질문인지 나눠서 알려준다
            if (feedbackRepository.findById(command.feedbackId()).isEmpty()) {
                throw new BusinessException(FeedbackErrorCode.FEEDBACK_NOT_FOUND);
            }
            throw new BusinessException(FeedbackErrorCode.FEEDBACK_NOT_IN_ROUND);
        }

        feedback.writeAnswer(command.answer(), answeredBy, now);
        return feedback;
    }

    private FeedbackCount getCountOf(FeedbackRound round) {
        return getCountOf(getCountMapOf(round.getYear()), round);
    }

    private FeedbackCount getCountOf(Map<FeedbackPeriod, FeedbackCount> counts, FeedbackRound round) {
        return counts.getOrDefault(
            FeedbackPeriod.of(round.getYear(), round.getRound()),
            FeedbackCount.empty(round.getYear(), round.getRound()));
    }

    private Map<FeedbackPeriod, FeedbackCount> getCountMapOf(Integer year) {
        return feedbackRepository.findCountsByYear(year).stream()
            .collect(Collectors.toMap(
                count -> FeedbackPeriod.of(count.year(), count.round()), Function.identity()));
    }

    private static PageResult<FeedbackRoundSummary> paginate(
        List<FeedbackRoundSummary> summaries, PageOffset pageOffset
    ) {
        int fromIndex = Math.min(pageOffset.page() * pageOffset.size(), summaries.size());
        int toIndex = Math.min(fromIndex + pageOffset.size(), summaries.size());

        return PageResult.of(
            summaries.subList(fromIndex, toIndex), pageOffset.page(), pageOffset.size(), summaries.size());
    }
}
