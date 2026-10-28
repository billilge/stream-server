package kr.ac.kookmin.stream.api.admin.welfare.feedback.usecase;

import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.welfare.domain.feedback.domain.Feedback;
import kr.ac.kookmin.stream.welfare.domain.feedback.service.AdminFeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 관리자 질문 목록. 검색어가 질문 내용과 작성자 이름 양쪽에 걸리는데 이름은 member 도메인에 있어
 * welfare가 조인할 수 없다. 그래서 ID를 징검다리로 쓴다 —
 * member에서 이름으로 회원 ID를 먼저 찾아 welfare에 넘기고(`created_by IN (...)`),
 * 페이지에 뜬 질문의 작성자만 다시 이름으로 바꾼다. member 모듈은 건드리지 않는다.
 */
@Component
@RequiredArgsConstructor
public class AdminFeedbackSearchUseCase {

    private final AdminFeedbackService adminFeedbackService;
    private final MemberService memberService;

    public PageResult<FeedbackWithStudent> search(Long roundId, String keyword, PageOffset pageOffset) {
        String searchKeyword = keyword == null || keyword.isBlank() ? null : keyword;
        List<Long> matchedMemberIds = searchKeyword == null
            ? List.of()
            : memberService.searchIdsByKeyword(searchKeyword);

        PageResult<Feedback> result =
            adminFeedbackService.searchQuestions(roundId, searchKeyword, matchedMemberIds, pageOffset);
        Map<Long, Member> students = memberService.getMapByIds(
            result.content().stream().map(Feedback::getCreatedBy).toList());

        return result.map(feedback -> new FeedbackWithStudent(feedback, students.get(feedback.getCreatedBy())));
    }
}
