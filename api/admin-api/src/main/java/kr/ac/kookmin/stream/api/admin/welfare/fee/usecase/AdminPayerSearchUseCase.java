package kr.ac.kookmin.stream.api.admin.welfare.fee.usecase;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.welfare.domain.fee.domain.Payer;
import kr.ac.kookmin.stream.welfare.domain.fee.service.PayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 납부자 명부에 회원 정보를 붙인다. 명부에는 학부도, 관리자로 등록됐는지도 없어 회원 쪽에서 가져온다 — 관리자 추가
 * 화면이 명부에서 사람을 고르면서 학부를 보여주고 이미 등록된 사람은 고르지 못하게 막는다.
 * <p>
 * 트랜잭션을 걸지 않는다. 쓰기가 없고, 조회 사이가 어긋나도 한 줄의 정보가 잠깐 최신이 아닐 뿐이다.
 */
@Component
@RequiredArgsConstructor
public class AdminPayerSearchUseCase {

    private final PayerService payerService;
    private final MemberService memberService;

    public PageResult<PayerMember> search(String keyword, PageOffset pageOffset) {
        PageResult<Payer> page = payerService.search(keyword, pageOffset);

        // 가입 전 레거시 이관 납부자는 memberId가 없고, 탈퇴한 회원은 조회에서 빠진다. 둘 다 member가 null로 남는다
        List<Long> memberIds = page.content().stream()
            .map(Payer::getMemberId)
            .filter(Objects::nonNull)
            .toList();
        Map<Long, Member> membersById = memberService.getMapByIds(memberIds);

        return page.map(payer -> new PayerMember(payer, membersById.get(payer.getMemberId())));
    }
}
