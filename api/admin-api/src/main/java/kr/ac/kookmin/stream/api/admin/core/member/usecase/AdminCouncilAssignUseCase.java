package kr.ac.kookmin.stream.api.admin.core.member.usecase;

import kr.ac.kookmin.stream.member.domain.member.domain.CouncilAssignCommand;
import kr.ac.kookmin.stream.member.domain.member.service.AdminMemberService;
import kr.ac.kookmin.stream.welfare.domain.fee.service.PayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminCouncilAssignUseCase {

    private final PayerService payerService;
    private final AdminMemberService adminMemberService;

    /**
     * 모두 학생회비 납부자인지 확인하고 운영진으로 지정한다. 운영진 화면이 납부자 명부에서 회원을 고르게 돼 있어
     * 같은 기준을 서버에서도 지킨다. 한 명이라도 납부자가 아니면 아무도 지정하지 않도록 전부 확인한 뒤 바꾼다.
     * <p>
     * 납부 여부는 회비 도메인이, 권한은 회원 도메인이 안다. 도메인끼리 부르지 않고 여기서 묶는다
     * ({@code architecture.md} 6-1절). 트랜잭션은 걸지 않는다 — 확인은 읽기뿐이라 되돌릴 것이 없다.
     */
    public void assign(CouncilAssignCommand command) {
        command.memberIds().forEach(payerService::validatePayer);
        adminMemberService.assignCouncil(command);
    }
}
