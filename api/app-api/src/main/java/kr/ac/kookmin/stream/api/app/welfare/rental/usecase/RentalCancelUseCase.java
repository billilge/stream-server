package kr.ac.kookmin.stream.api.app.welfare.rental.usecase;

import kr.ac.kookmin.stream.common.LockExecutor;
import kr.ac.kookmin.stream.common.OutboxWriter;
import kr.ac.kookmin.stream.common.event.RentalApplicationCanceled;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.service.ItemService;
import kr.ac.kookmin.stream.welfare.domain.rental.service.RentalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 대여 신청 취소. {@code rental}(상태 전이)과 {@code item}(재고 복구) 두 집합체를 조합한다. */
@Component
@RequiredArgsConstructor
public class RentalCancelUseCase {

    private final LockExecutor lockExecutor;
    private final RentalHistoryService rentalHistoryService;
    private final ItemService itemService;
    private final OutboxWriter outboxWriter;

    public void cancel(Long memberId, Long historyId) {
        lockExecutor.executeOptimistic(() -> {
            RentalHistory history = rentalHistoryService.cancel(memberId, historyId);
            Item item = itemService.increaseStock(history.getItemId(), history.getRentedCount());

            outboxWriter.record(new RentalApplicationCanceled(memberId, history.getId(), item.getName()));
        });
    }
}
