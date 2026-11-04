package kr.ac.kookmin.stream.api.admin.welfare.rental.usecase;

import kr.ac.kookmin.stream.common.LockExecutor;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemType;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;
import kr.ac.kookmin.stream.welfare.domain.rental.service.AdminRentalHistoryService;
import kr.ac.kookmin.stream.welfare.domain.rental.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 관리자 대여 기록 삭제. {@code rental}(이력·처리자 로그 삭제)과 {@code item}(재고 복구) 두 도메인을 조합한다. */
@Component
@RequiredArgsConstructor
public class AdminRentalRecordDeleteUseCase {

    private final LockExecutor lockExecutor;
    private final AdminRentalHistoryService adminRentalHistoryService;
    private final ItemService itemService;

    public void delete(Long historyId) {
        lockExecutor.executeOptimistic(() -> {
            RentalRecord record = adminRentalHistoryService.getById(historyId);

            // 잘못 등록한 기록을 없던 일로 되돌리므로, 재고를 차지하고 있던 기록이면 삭제 전에 재고를 복구한다
            Item item = record.item();
            ItemType itemType = item == null ? null : item.getType();
            if (record.history().getRentalStatus().holdsStock(itemType)) {
                itemService.increaseStock(record.history().getItemId(), record.history().getRentedCount());
            }

            adminRentalHistoryService.delete(historyId);
        });
    }
}
