package kr.ac.kookmin.stream.api.admin.event.event.usecase;

import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationAnswer;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.EventManageDetail;
import kr.ac.kookmin.stream.event.domain.event.service.AdminEventService;
import kr.ac.kookmin.stream.event.domain.event.service.EventApplicationService;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 운영진 신청 현황. 신청은 행사(event) 도메인, 이름·학번은 회원(member) 도메인이라 이 UseCase가 조합한다.
 * 조회만 하므로 트랜잭션으로 묶지 않는다.
 */
@Component
@RequiredArgsConstructor
public class AdminEventApplicationQueryUseCase {

    private final AdminEventService adminEventService;
    private final EventApplicationService eventApplicationService;
    private final MemberService memberService;

    /**
     * @param keyword 이름 또는 학번 검색어. {@code null}이면 검색하지 않는다
     * @param status  {@code null}이면 상태로 거르지 않는다
     */
    public EventApplicantPage getApplicants(
        Long eventId,
        String keyword,
        EventApplicationStatus status,
        PageOffset pageOffset
    ) {
        EventManageDetail event = adminEventService.getEvent(eventId);
        long totalApplicationCount = eventApplicationService.countApplications(eventId);

        PageResult<EventApplication> applications = searchApplications(eventId, keyword, status, pageOffset);
        List<Long> applicationIds = applications.content().stream().map(EventApplication::getId).toList();
        Map<Long, List<EventApplicationAnswer>> answers =
            eventApplicationService.getAnswersByApplicationIds(applicationIds);
        // 탈퇴한 회원은 결과에서 빠지므로 그 회원의 신청은 member가 null로 남는다
        Map<Long, Member> members = memberService.getMapByIds(
            applications.content().stream().map(EventApplication::getMemberId).distinct().toList());

        return new EventApplicantPage(event, totalApplicationCount, applications.map(application -> new EventApplicant(
            application,
            members.get(application.getMemberId()),
            answers.getOrDefault(application.getId(), List.of())
        )));
    }

    public EventApplicantDetail getApplicant(Long eventId, Long applicationId) {
        EventManageDetail event = adminEventService.getEvent(eventId);
        EventApplication application = eventApplicationService.getApplication(eventId, applicationId);
        List<EventApplicationAnswer> answers = eventApplicationService
            .getAnswersByApplicationIds(List.of(applicationId))
            .getOrDefault(applicationId, List.of());
        Member member = memberService.getMapByIds(List.of(application.getMemberId())).get(application.getMemberId());

        return new EventApplicantDetail(
            application,
            member,
            EventApplicationDetail.of(event.event(), application, event.questions(), answers)
        );
    }

    // 검색어에 맞는 회원이 없으면 신청을 조회할 필요도 없다
    private PageResult<EventApplication> searchApplications(
        Long eventId,
        String keyword,
        EventApplicationStatus status,
        PageOffset pageOffset
    ) {
        if (keyword == null) {
            return eventApplicationService.getApplications(eventId, status, null, pageOffset);
        }
        List<Long> memberIds = memberService.searchIdsByKeyword(keyword);
        if (memberIds.isEmpty()) {
            return PageResult.empty(pageOffset.page(), pageOffset.size());
        }
        return eventApplicationService.getApplications(eventId, status, memberIds, pageOffset);
    }
}
