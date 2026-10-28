package kr.ac.kookmin.stream.welfare.domain.feedback.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.Feedback;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackAnswerCommand;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRoundDetail;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRoundStatus;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRoundSummary;

/**
 * 운영진용 열린피드백 서비스. 회차 등록·조회, 질문 검색, 답변 임시저장·공개를 맡는다.
 * 학생 앱의 질문 조회·작성은 {@link FeedbackService}가 맡는다.
 */
public interface AdminFeedbackService {

    // 접수 기간만 받아 회차를 연다. 연도는 시작일 기준, 회차 번호는 그 연도의 최대 + 1로 서버가 배정한다
    FeedbackRoundSummary createRound(LocalDate opensOn, LocalDate closesOn);

    // 답변을 아직 공개하지 않은 가장 최근 회차. 그런 회차가 없으면 비어 있다
    Optional<FeedbackRoundSummary> getCurrentRound();

    // year/status가 null이면 그 조건은 필터하지 않는다
    PageResult<FeedbackRoundSummary> getRounds(Integer year, FeedbackRoundStatus status, PageOffset pageOffset);

    FeedbackRoundDetail getRoundDetail(Long roundId);

    // roundId가 null이면 진행 중 회차를 대상으로 한다. 진행 중 회차도 없으면 빈 페이지
    PageResult<Feedback> searchQuestions(
        Long roundId, String keyword, List<Long> memberIds, PageOffset pageOffset);

    // 답변 임시저장. 보낸 것만 반영하고 기존 답변은 덮어쓴다. 여기서 저장한 답변은 아직 학생에게 보이지 않는다
    FeedbackRoundDetail saveAnswers(Long roundId, List<FeedbackAnswerCommand> commands, Long answeredBy);

    // 그 회차에 작성된 답변을 학생에게 공개한다. 이미 공개한 회차면 공개 시각을 바꾸지 않는다
    FeedbackRoundDetail publishAnswers(Long roundId);
}
