package kr.ac.kookmin.stream.api.admin.welfare.rental.usecase;

import java.util.List;
import kr.ac.kookmin.stream.api.admin.welfare.rental.response.AdminRentalHistoryResponse;
import kr.ac.kookmin.stream.common.LockExecutor;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemType;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalHistory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalRecord;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalStatus;
import kr.ac.kookmin.stream.welfare.domain.rental.service.AdminRentalHistoryService;
import kr.ac.kookmin.stream.welfare.domain.rental.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 관리자 대여 상태 변경. {@code rental}(상태 전이, 처리자 로그)과 {@code item}(재고 복구) 두 도메인을 조합한다. */
@Component
@RequiredArgsConstructor
public class AdminRentalStatusUpdateUseCase {

    private final LockExecutor lockExecutor;
    private final MemberService memberService;
    private final ItemService itemService;
    private final AdminRentalHistoryService adminRentalHistoryService;

    public AdminRentalHistoryResponse updateStatus(Long historyId, RentalStatus requestedStatus, Long workerId) {
        return lockExecutor.executeOptimistic(() -> {
            memberService.getById(workerId);

            RentalRecord current = adminRentalHistoryService.getById(historyId);
            RentalStatus previousStatus = current.history().getRentalStatus();
            Item item = current.item();

            boolean isConsumable = requestedStatus == RentalStatus.CONFIRMED
                && item != null && item.getType() == ItemType.CONSUMABLE;
            RentalStatus newStatus = isConsumable ? RentalStatus.RETURNED : requestedStatus;

            RentalHistory history = adminRentalHistoryService.changeStatusByAdmin(historyId, newStatus, workerId);

            if (shouldRestock(previousStatus, newStatus, item)) {
                item = itemService.increaseStock(history.getItemId(), history.getRentedCount());
            }

            Member renter = memberService.getMapByIds(List.of(history.getMemberId())).get(history.getMemberId());
            return AdminRentalHistoryResponse.from(new RentalRecord(history, item), renter);
        });
    }

    private boolean shouldRestock(RentalStatus previousStatus, RentalStatus newStatus, Item item) {
        if (previousStatus.isFinished()) {
            return false;
        }
        if (newStatus == RentalStatus.CANCEL || newStatus == RentalStatus.REJECTED) {
            return true;
        }
        return newStatus == RentalStatus.RETURNED && item != null && item.getType() == ItemType.RENTAL;
    }
}
