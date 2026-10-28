package kr.ac.kookmin.stream.api.app.welfare.rental.usecase;

import kr.ac.kookmin.stream.common.LockExecutor;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.service.ItemService;
import kr.ac.kookmin.stream.welfare.domain.rental.service.RentalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 반납 신청. {@code rental}(상태 전이)과 {@code item}(재고 복구) 두 집합체를 조합한다 — {@code RentalApplyUseCase}와
 * 같은 이유로 {@code RentalHistoryServiceImpl}이 {@code ItemService}를 직접 부르지 않는다.
 * <p>
 * {@code increaseStock}도 {@code decreaseStock}과 같은 낙관적 락(DIRTY) 충돌 감지를 받는다 — "상한이 없다"는
 * 비즈니스 규칙이 없다는 뜻일 뿐, 같은 물품에 다른 트랜잭션이 먼저 count를 바꾸면 똑같이 충돌한다. 그래서
 * {@code RentalApplyUseCase}와 같은 이유로 {@code LockExecutor}로 감싼다.
 */
@Component
@RequiredArgsConstructor
public class RentalReturnUseCase {

    private final LockExecutor lockExecutor;
    private final RentalHistoryService rentalHistoryService;
    private final ItemService itemService;

    // @Transactional을 걸지 않는다. LockExecutor가 시도마다 새 트랜잭션을 연다 — 같은 트랜잭션 안에서 재시도하면
    // 1차 캐시가 예전 값을 계속 돌려줘 매번 다시 충돌하므로, 충돌한 시도는 전부 롤백되고 새 트랜잭션에서
    // 반납 대상 조회부터 다시 실행된다(RentalHistory는 롤백됐으니 여전히 RENTAL/OVERDUE 상태라 다시 찾힌다).
    public void returnRental(Long memberId, Long historyId) {
        lockExecutor.executeOptimistic(() -> {
            RentalHistory history = rentalHistoryService.returnRental(memberId, historyId);
            itemService.increaseStock(history.getItemId(), history.getRentedCount());
        });
    }
}
