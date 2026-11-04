package kr.ac.kookmin.stream.api.admin.welfare.fee.usecase;

import java.util.List;
import java.util.Map;
import java.util.Set;
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
 * 페이징과 상태 필터는 <b>회비 쪽에서</b> 한다. 납부를 요청한 회원만 행이 있으므로 목록도 요청한 회원만 나온다
 * (한 번도 요청하지 않은 미납부 회원은 관리자 화면에서 다루지 않는다).
 * <p>
 * member 쪽은 공개 조회 메서드만 쓴다 — 검색은 {@code searchIdsByKeyword}, 회원 정보는 {@code getMapByIds},
 * 가입 여부는 {@code findSignedUpIds}. 회원 정보와 가입 여부는 한 페이지의 회원을 한 번에 조회한다.
 * <p>
 * 트랜잭션을 걸지 않는다. 쓰기가 없고, 조회 사이가 어긋나도 한 줄의 정보가 잠깐 최신이 아닐 뿐이다.
 */
@Component
@RequiredArgsConstructor
public class AdminFeeSearchUseCase {

    private final StudentTransferStatusService studentTransferStatusService;
    private final MemberService memberService;

    public PageResult<MemberFeeStatus> search(TransferStatus status, String keyword, PageOffset pageOffset) {
        List<Long> memberIds = null;
        if (keyword != null && !keyword.isBlank()) {
            memberIds = memberService.searchIdsByKeyword(keyword);
            if (memberIds.isEmpty()) {
                return PageResult.empty(pageOffset.page(), pageOffset.size());
            }
        }

        PageResult<StudentTransferStatus> page = studentTransferStatusService.search(status, memberIds, pageOffset);
        List<Long> pagedMemberIds = page.content().stream().map(StudentTransferStatus::getMemberId).toList();

        // 탈퇴(소프트 삭제)한 회원은 둘 다에서 빠지므로 그 회원의 행은 member가 null, 가입 여부는 false로 남는다
        Map<Long, Member> membersById = memberService.getMapByIds(pagedMemberIds);
        Set<Long> signedUpIds = memberService.findSignedUpIds(pagedMemberIds);

        return page.map(transferStatus -> new MemberFeeStatus(
            transferStatus,
            membersById.get(transferStatus.getMemberId()),
            signedUpIds.contains(transferStatus.getMemberId())
        ));
    }
}
