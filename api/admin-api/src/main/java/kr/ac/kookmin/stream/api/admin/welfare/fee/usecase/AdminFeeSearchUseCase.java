package kr.ac.kookmin.stream.api.admin.welfare.fee.usecase;

import java.util.List;
import java.util.Map;
import java.util.Set;
import kr.ac.kookmin.stream.api.admin.welfare.fee.FeeAccount;
import kr.ac.kookmin.stream.api.admin.welfare.fee.FeeAccountProvider;
import kr.ac.kookmin.stream.api.admin.welfare.fee.response.AdminFeeSearchResponse;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.StudentTransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.TransferStatus;
import kr.ac.kookmin.stream.welfare.domain.fee.service.StudentTransferStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 납부자 목록. {@code member}와 {@code welfare} 두 도메인을 조합한다.
 * <p>
 * <b>회원을 기준으로 페이징한다.</b> 납부 정보를 내지 않은 회원은 {@code student_transfer_statuses}에
 * 행이 없어서, 납부 쪽을 기준으로 조회하면 "납부 전" 학생이 목록에서 통째로 빠지기 때문이다.
 * 상태 필터도 같은 이유로 {@code members.fee_status}에 건다.
 * <p>
 * 트랜잭션을 걸지 않는다 — 쓰기가 없고, 세 조회가 어긋나도 한 줄의 납부 정보가 잠깐 최신이 아닐 뿐이다.
 */
@Component
@RequiredArgsConstructor
public class AdminFeeSearchUseCase {

    private final MemberService memberService;
    private final StudentTransferStatusService studentTransferStatusService;
    private final FeeAccountProvider feeAccountProvider;

    public PageResult<AdminFeeSearchResponse> search(TransferStatus status, String keyword, PageOffset pageOffset) {
        PageResult<Member> members =
            memberService.searchByFeeStatus(status == null ? null : status.name(), keyword, pageOffset);

        List<Long> memberIds = members.content().stream().map(Member::getId).toList();
        Map<Long, StudentTransferStatus> transferStatusByMemberId =
            studentTransferStatusService.findMapByMemberIds(memberIds);
        Set<Long> signedUpMemberIds = memberService.findSignedUpMemberIds(memberIds);

        // 학기당 금액은 줄마다 같으므로 목록당 한 번만 읽는다
        FeeAccount account = feeAccountProvider.getAccount();

        return members.map(member -> AdminFeeSearchResponse.of(
            member,
            transferStatusByMemberId.get(member.getId()),
            signedUpMemberIds.contains(member.getId()),
            account
        ));
    }
}
