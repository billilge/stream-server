package kr.ac.kookmin.stream.api.admin.welfare.fee.usecase;

import java.util.List;
import java.util.Map;
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
 * 납부자 목록. {@code welfare}와 {@code member} 두 도메인을 조합한다.
 * <p>
 * 페이징과 상태 필터는 <b>회비 쪽에서</b> 한다. 모든 회원이 납부 상태 행을 갖기 때문에(로그인 때 생성,
 * {@code V21}로 기존 회원 백필) 납부 전 회원도 그대로 걸린다. 회원 기준으로 뒤집으면 member 도메인이
 * 회비 상태를 알아야 하는데, 회비 때문에 남의 도메인을 고치지 않으려고 방향을 이렇게 잡았다.
 * <p>
 * member 쪽은 이미 공개된 조회 메서드만 쓴다 — 검색은 {@code searchIdsByKeyword}, 회원 정보는 {@code getMapByIds}.
 * <p>
 * 트랜잭션을 걸지 않는다. 쓰기가 없고, 조회 사이가 어긋나도 한 줄의 정보가 잠깐 최신이 아닐 뿐이다.
 */
@Component
@RequiredArgsConstructor
public class AdminFeeSearchUseCase {

    private final StudentTransferStatusService studentTransferStatusService;
    private final MemberService memberService;
    private final FeeAccountProvider feeAccountProvider;

    public PageResult<AdminFeeSearchResponse> search(TransferStatus status, String keyword, PageOffset pageOffset) {
        List<Long> memberIds = null;
        if (keyword != null && !keyword.isBlank()) {
            memberIds = memberService.searchIdsByKeyword(keyword);
            if (memberIds.isEmpty()) {
                return PageResult.empty(pageOffset.page(), pageOffset.size());
            }
        }

        PageResult<StudentTransferStatus> page = studentTransferStatusService.search(status, memberIds, pageOffset);
        List<Long> pagedMemberIds = page.content().stream().map(StudentTransferStatus::getMemberId).toList();

        // 탈퇴(소프트 삭제)한 회원은 빠지므로 그 회원의 행은 member가 null로 남는다
        Map<Long, Member> membersById = memberService.getMapByIds(pagedMemberIds);
        // 학기당 금액은 줄마다 같으므로 목록당 한 번만 읽는다
        FeeAccount account = feeAccountProvider.getAccount();

        return page.map(transferStatus -> AdminFeeSearchResponse.of(
            transferStatus,
            membersById.get(transferStatus.getMemberId()),
            // 가입 여부는 member가 아는 사실이라 member의 공개 메서드에 묻는다.
            // 줄마다 한 번씩 부르므로 목록이 커지면 벌크 조회를 member 쪽에 요청할 자리다
            memberService.isSignedUp(transferStatus.getMemberId()),
            account
        ));
    }
}
