package kr.ac.kookmin.stream.api.app.welfare.rental.usecase;

import kr.ac.kookmin.stream.common.LockExecutor;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.service.ItemService;
import kr.ac.kookmin.stream.welfare.domain.rental.service.RentalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 대여 신청 취소. {@code rental}(상태 전이)과 {@code item}(재고 복구) 두 집합체를 조합한다 — 운영진이 아직
 * 손대지 않은(PENDING) 신청만 대상이라 재고 복구가 항상 일어난다. {@code RentalApplyUseCase}와 같은 이유로
 * {@code increaseStock}이 받는 낙관적 락 충돌 감지 때문에 {@code LockExecutor}로 감싼다.
 */
@Component
@RequiredArgsConstructor
public class RentalCancelUseCase {

    private final LockExecutor lockExecutor;
    private final RentalHistoryService rentalHistoryService;
    private final ItemService itemService;

    public void cancel(Long memberId, Long historyId) {
        lockExecutor.executeOptimistic(() -> {
            RentalHistory history = rentalHistoryService.cancel(memberId, historyId);
            itemService.increaseStock(history.getItemId(), history.getRentedCount());
        });
    }
}
