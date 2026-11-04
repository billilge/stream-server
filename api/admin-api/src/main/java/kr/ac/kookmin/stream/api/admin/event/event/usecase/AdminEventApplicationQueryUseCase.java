package kr.ac.kookmin.stream.api.admin.event.event.usecase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplication;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationAnswer;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationManageDetail;
import kr.ac.kookmin.stream.event.domain.event.domain.EventApplicationStatus;
import kr.ac.kookmin.stream.event.domain.event.domain.EventManageDetail;
import kr.ac.kookmin.stream.event.domain.event.service.AdminEventApplicationService;
import kr.ac.kookmin.stream.event.domain.event.service.AdminEventService;
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

    // 엑셀 내보내기에서 신청 현황을 한 번에 읽어 올 개수. 목록 화면의 페이지 크기 상한과 같다
    private static final int EXPORT_PAGE_SIZE = 100;

    private final AdminEventService adminEventService;
    private final AdminEventApplicationService adminEventApplicationService;
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
        long totalApplicationCount = adminEventApplicationService.countApplications(eventId);

        PageResult<EventApplication> applications = searchApplications(eventId, keyword, status, pageOffset);

        return new EventApplicantPage(
            event, totalApplicationCount, applications.map(toApplicant(event, applications.content())));
    }

    /**
     * 엑셀 내보내기. 신청 현황과 같은 조건의 신청을 페이지 없이 모두 담는다. 신청 현황의 조회를 페이지마다 그대로 불러
     * 목록과 내보내기의 검색·정렬 기준이 어긋나지 않게 한다.
     *
     * @param keyword 이름 또는 학번 검색어. {@code null}이면 검색하지 않는다
     * @param status  {@code null}이면 상태로 거르지 않는다
     */
    public EventApplicantList getAllApplicants(Long eventId, String keyword, EventApplicationStatus status) {
        EventManageDetail event = adminEventService.getEvent(eventId);

        List<EventApplication> applications = new ArrayList<>();
        for (int page = 0; ; page++) {
            PageResult<EventApplication> result =
                searchApplications(eventId, keyword, status, PageOffset.of(page, EXPORT_PAGE_SIZE));
            applications.addAll(result.content());
            if (page + 1 >= result.totalPage()) {
                break;
            }
        }

        return new EventApplicantList(event, applications.stream().map(toApplicant(event, applications)).toList());
    }

    public EventApplicantDetail getApplicant(Long eventId, Long applicationId) {
        EventApplicationManageDetail detail = adminEventApplicationService.getApplicationDetail(eventId, applicationId);
        EventApplication application = detail.application();
        Member member = memberService.getMapByIds(List.of(application.getMemberId())).get(application.getMemberId());

        return new EventApplicantDetail(application, member, detail.detail());
    }

    // 신청마다 신청자와 답변을 붙인다. 답변과 회원은 넘겨받은 신청 전체를 한 번에 조회한다
    private Function<EventApplication, EventApplicant> toApplicant(
        EventManageDetail event,
        List<EventApplication> applications
    ) {
        List<Long> applicationIds = applications.stream().map(EventApplication::getId).toList();
        Map<Long, List<EventApplicationAnswer>> answers =
            adminEventApplicationService.getAnswersByApplicationIds(applicationIds);
        // 탈퇴한 회원은 결과에서 빠지므로 그 회원의 신청은 member가 null로 남는다
        Map<Long, Member> members = memberService.getMapByIds(
            applications.stream().map(EventApplication::getMemberId).distinct().toList());

        return application -> new EventApplicant(
            application,
            members.get(application.getMemberId()),
            EventApplicationDetail.AnsweredQuestion.listOf(
                event.questions(), answers.getOrDefault(application.getId(), List.of()))
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
            return adminEventApplicationService.getApplications(eventId, status, null, pageOffset);
        }
        List<Long> memberIds = memberService.searchIdsByKeyword(keyword);
        if (memberIds.isEmpty()) {
            return PageResult.empty(pageOffset.page(), pageOffset.size());
        }
        return adminEventApplicationService.getApplications(eventId, status, memberIds, pageOffset);
    }
}
