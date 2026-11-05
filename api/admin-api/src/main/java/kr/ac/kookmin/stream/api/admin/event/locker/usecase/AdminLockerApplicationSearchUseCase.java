package kr.ac.kookmin.stream.api.admin.event.locker.usecase;

import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationCancelStatus;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationCounts;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplicationResult;
import kr.ac.kookmin.stream.event.domain.locker.service.AdminLockerService;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminLockerApplicationSearchUseCase {

    private final AdminLockerService adminLockerService;
    private final MemberService memberService;

    /**
     * 운영 회차의 신청 현황에 신청자 정보와 상단 개수를 붙인다. 상태로 좁히지 않으면 취소된 신청도 담는다.
     *
     * @param keyword 이름·학번 일부. 비어 있으면 회차 전체를 본다
     * @param status  {@code null}이면 상태로 거르지 않는다
     */
    public LockerApplicantPage search(
        Long lockerPeriodId, String keyword, LockerApplicationCancelStatus status, PageOffset pageOffset
    ) {
        LockerApplicationCounts counts = adminLockerService.getApplicationCounts(lockerPeriodId);
        List<Long> memberIds = null;

        if (keyword != null && !keyword.isBlank()) {
            memberIds = memberService.searchIdsByKeyword(keyword);

            if (memberIds.isEmpty()) {
                return new LockerApplicantPage(counts, PageResult.empty(pageOffset.page(), pageOffset.size()));
            }
        }

        PageResult<LockerApplicationResult> result =
            adminLockerService.getApplications(lockerPeriodId, memberIds, status, pageOffset);
        List<Long> resultMemberIds = result.content().stream()
            .map(applicationResult -> applicationResult.application().getMemberId())
            .toList();

        // 탈퇴(소프트 삭제)한 회원은 getMapByIds에서 빠지므로, 그 회원의 지난 신청은 member가 null로 남는다
        Map<Long, Member> memberMap = memberService.getMapByIds(resultMemberIds);

        return new LockerApplicantPage(counts, result.map(applicationResult -> new MemberLockerApplication(
            applicationResult, memberMap.getOrDefault(applicationResult.application().getMemberId(), null))));
    }
}
