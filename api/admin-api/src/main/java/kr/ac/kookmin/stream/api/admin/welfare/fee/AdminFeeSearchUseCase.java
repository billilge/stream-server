package kr.ac.kookmin.stream.api.admin.welfare.fee;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.AdminFeeSearchResponse;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.service.FeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminFeeSearchUseCase {

    private final FeeService feeService;
    private final MemberService memberService;

    @Transactional(readOnly = true)
    public PageResult<AdminFeeSearchResponse> search(String status, String keyword, int page, int size) {
        List<Long> memberIds = null;
        if (keyword != null && !keyword.isBlank()) {
            memberIds = memberService.searchIdsByKeyword(keyword);
            if (memberIds.isEmpty()) {
                return new PageResult<>(List.of(), page, size, 0, 0);
            }
        }

        PageResult<StudentTransferStatus> result = feeService.search(status, memberIds, page, size);
        Map<Long, Member> membersById = memberService.findAllByIds(
            result.content().stream().map(StudentTransferStatus::getMemberId).toList()
        ).stream().collect(Collectors.toMap(Member::getId, Function.identity()));

        List<AdminFeeSearchResponse> content = result.content().stream()
            .map(request -> AdminFeeSearchResponse.of(request, membersById.get(request.getMemberId())))
            .toList();

        return new PageResult<>(content, result.page(), result.size(), result.totalCount(), result.totalPage());
    }
}
