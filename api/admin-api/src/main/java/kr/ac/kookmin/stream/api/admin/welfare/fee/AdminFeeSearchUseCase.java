package kr.ac.kookmin.stream.api.admin.welfare.fee;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.service.StudentTransferStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminFeeSearchUseCase {

    private final StudentTransferStatusService studentTransferStatusService;
    private final MemberService memberService;

    @Transactional(readOnly = true)
    public PageResult<MemberFeeStatus> search(TransferStatus status, String keyword, int page, int size) {
        List<Long> memberIds = null;
        if (keyword != null && !keyword.isBlank()) {
            memberIds = memberService.searchIdsByKeyword(keyword);
            if (memberIds.isEmpty()) {
                return PageResult.empty(page, size);
            }
        }

        PageResult<StudentTransferStatus> result = studentTransferStatusService.search(status, memberIds, page, size);
        // 탈퇴(소프트 삭제)한 회원은 findAllByIds에서 빠지므로, 그 회원의 과거 요청은 member가 null로 남는다.
        // (탈퇴 회원의 요청을 조회 자체에서 제외하는 대신, 표시는 Controller/DTO에서 처리한다)
        Map<Long, Member> membersById = memberService.findAllByIds(
            result.content().stream().map(StudentTransferStatus::getMemberId).toList()
        ).stream().collect(Collectors.toMap(Member::getId, Function.identity()));

        return result.map(transferStatus -> new MemberFeeStatus(transferStatus, membersById.get(transferStatus.getMemberId())));
    }
}
