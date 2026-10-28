package kr.ac.kookmin.stream.api.admin.event.locker.usecase;

import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.event.domain.locker.domain.LockerApplication;
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
     * 운영 회차의 신청 현황에 신청자 정보를 붙인다. 취소된 신청도 담는다.
     *
     * @param keyword 이름·학번 일부. 비어 있으면 회차 전체를 본다
     */
    public PageResult<MemberLockerApplication> search(Long lockerPeriodId, String keyword, PageOffset pageOffset) {
        List<Long> memberIds = null;

        if (keyword != null && !keyword.isBlank()) {
            memberIds = memberService.searchIdsByKeyword(keyword);

            if (memberIds.isEmpty()) {
                return PageResult.empty(pageOffset.page(), pageOffset.size());
            }
        }

        PageResult<LockerApplicationResult> result =
            adminLockerService.getApplications(lockerPeriodId, memberIds, pageOffset);
        List<Long> resultMemberIds = result.content().stream()
            .map(applicationResult -> applicationResult.application().getMemberId())
            .toList();

        // 탈퇴(소프트 삭제)한 회원은 getMapByIds에서 빠지므로, 그 회원의 지난 신청은 member가 null로 남는다
        Map<Long, Member> memberMap = memberService.getMapByIds(resultMemberIds);

        return result.map(applicationResult -> new MemberLockerApplication(
            applicationResult, memberMap.getOrDefault(applicationResult.application().getMemberId(), null)));
    }
}
