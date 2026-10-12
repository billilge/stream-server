package kr.ac.kookmin.stream.api.app.welfare.rental.usecase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.DateUtil;
import kr.ac.kookmin.stream.common.LockExecutor;
import kr.ac.kookmin.stream.welfare.domain.fee.service.PayerService;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalErrorCode;
import kr.ac.kookmin.stream.welfare.domain.rental.service.ItemService;
import kr.ac.kookmin.stream.welfare.domain.rental.service.RentalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 대여 신청. {@code fee}(회비 납부 확인)와 {@code rental}(재고 차감, 이력 생성) 두 도메인을 조합한다.
 * 대여 시간(영업시간·점심시간) 검증은 DB에 닿지 않으므로 트랜잭션을 열기 전, 컨트롤러에서 끝낸다.
 */
@Component
@RequiredArgsConstructor
public class RentalApplyUseCase {

    private final LockExecutor lockExecutor;
    private final PayerService payerService;
    private final ItemService itemService;
    private final RentalHistoryService rentalHistoryService;

    // @Transactional을 걸지 않는다. 트랜잭션은 lockExecutor가 시도마다 새로 연다 — 재고 차감이 충돌하면
    // 같은 트랜잭션에서는 예전 재고만 다시 읽혀 재시도가 소용없어서다. 회비 확인부터 이력 생성까지는 시도마다 한 트랜잭션이다.
    // 같은 물품에 동시에 들어온 신청은 재고 충돌로 늦은 쪽이 처음부터 다시 실행되므로, 재고 경합과 함께
    // 같은 회원의 같은 물품 중복 대여 경합도 막힌다(재시도는 먼저 커밋된 이력을 보고 중복으로 거절한다).
    public void apply(Long memberId, Long itemId, int count, int rentAtHour, int rentAtMinute, boolean ignoreDuplicate) {
        lockExecutor.executeOptimistic(() -> {
            payerService.validatePayer(memberId);

            if (!ignoreDuplicate && rentalHistoryService.existsActiveRental(itemId, memberId)) {
                throw new BusinessException(RentalErrorCode.RENTAL_ITEM_DUPLICATED);
            }

            Item item = itemService.decreaseStock(itemId, count);

            LocalDateTime now = LocalDateTime.now(DateUtil.KST);
            LocalDateTime rentAt = LocalDate.now(DateUtil.KST).atTime(rentAtHour, rentAtMinute);
            rentalHistoryService.create(item, memberId, count, now, rentAt);
        });
    }
}
