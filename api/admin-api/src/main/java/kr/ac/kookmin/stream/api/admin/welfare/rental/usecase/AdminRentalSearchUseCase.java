package kr.ac.kookmin.stream.api.admin.welfare.rental.usecase;

import java.util.List;
import java.util.Map;
import kr.ac.kookmin.stream.api.admin.welfare.rental.response.AdminRentalHistoryResponse;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;
import kr.ac.kookmin.stream.welfare.domain.rental.service.RentalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 대여·반납 현황 조회. {@code AdminFeeSearchUseCase}와 같은 패턴 — 키워드로 회원 id를 먼저 찾아 필터로 넘긴다. */
@Component
@RequiredArgsConstructor
public class AdminRentalSearchUseCase {

    private final RentalHistoryService rentalHistoryService;
    private final MemberService memberService;

    public PageResult<AdminRentalHistoryResponse> search(RentalStatus status, String keyword, PageOffset pageOffset) {
        List<Long> memberIds = null;

        if (keyword != null && !keyword.isBlank()) {
            memberIds = memberService.searchIdsByKeyword(keyword);

            if (memberIds.isEmpty()) {
                return PageResult.empty(pageOffset.page(), pageOffset.size());
            }
        }

        PageResult<RentalRecord> result = rentalHistoryService.search(status, memberIds, pageOffset);

        List<Long> resultMemberIds = result.content().stream().map(record -> record.history().getMemberId()).distinct().toList();
        Map<Long, Member> memberMap = memberService.getMapByIds(resultMemberIds);

        return result.map(record -> AdminRentalHistoryResponse.from(record, memberMap.get(record.history().getMemberId())));
    }
}
