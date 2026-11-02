package kr.ac.kookmin.stream.api.admin.event.locker.usecase;

import kr.ac.kookmin.stream.event.domain.locker.service.AdminLockerService;
import kr.ac.kookmin.stream.welfare.domain.fee.service.PayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminLockerAssignUseCase {

    private final PayerService payerService;
    private final AdminLockerService adminLockerService;

    /**
     * 학생회비 납부자인지 확인하고 사물함을 배정한다. 운영진 화면이 납부자 목록에서 학생을 고르게 돼 있어 같은 기준을
     * 서버에서도 지킨다.
     * <p>
     * 납부 여부는 회비 도메인이, 배정은 사물함 도메인이 안다. 도메인끼리 부르지 않고 여기서 묶는다
     * ({@code architecture.md} 6-1절).
     * <p>
     * 트랜잭션을 걸지 않는다. 납부자 확인은 읽기이고 그 전에 쓰는 것이 없어, 실패해도 되돌릴 것이 없다.
     * 쓰기는 {@code assignLocker}가 자기 트랜잭션에서 한다.
     */
    public void assign(Long lockerPeriodId, Long memberId, Long lockerId) {
        payerService.validatePayer(memberId);
        adminLockerService.assignLocker(lockerPeriodId, memberId, lockerId);
    }
}
