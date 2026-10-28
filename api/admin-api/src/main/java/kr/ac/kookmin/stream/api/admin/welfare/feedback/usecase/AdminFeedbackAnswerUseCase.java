package kr.ac.kookmin.stream.api.admin.welfare.feedback.usecase;

import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.Feedback;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackAnswerCommand;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.FeedbackRoundDetail;
import kr.ac.kookmin.stream.welfare.domain.feedback.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// 회차 답변 화면이 쓰는 조합. welfare가 돌려준 질문·답변에 member의 작성자 이름을 붙인다.
@Component
@RequiredArgsConstructor
public class AdminFeedbackAnswerUseCase {

    private final FeedbackService feedbackService;
    private final MemberService memberService;

    public FeedbackRoundAnswers getRoundAnswers(Long roundId) {
        return withStudents(feedbackService.getRoundDetail(roundId));
    }

    public FeedbackRoundAnswers saveAnswers(Long roundId, List<FeedbackAnswerCommand> commands, Long answeredBy) {
        return withStudents(feedbackService.saveAnswers(roundId, commands, answeredBy));
    }

    public FeedbackRoundAnswers publishAnswers(Long roundId) {
        return withStudents(feedbackService.publishAnswers(roundId));
    }

    private FeedbackRoundAnswers withStudents(FeedbackRoundDetail detail) {
        Map<Long, Member> students = memberService.getMapByIds(
            detail.feedbacks().stream().map(Feedback::getCreatedBy).toList());

        return new FeedbackRoundAnswers(
            detail.summary(),
            detail.feedbacks().stream()
                .map(feedback -> new FeedbackWithStudent(feedback, students.get(feedback.getCreatedBy())))
                .toList()
        );
    }
}
