package kr.ac.kookmin.stream.api.admin.welfare.rental.usecase;

import java.util.List;
import kr.ac.kookmin.stream.api.admin.welfare.rental.response.AdminRentalHistoryResponse;
import kr.ac.kookmin.stream.common.LockExecutor;
import kr.ac.kookmin.stream.common.Role;
import kr.ac.kookmin.stream.member.domain.member.domain.Member;
import kr.ac.kookmin.stream.member.domain.member.service.MemberService;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemType;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalErrorCode;
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
            validateIsAdmin(workerId);

            RentalRecord current = adminRentalHistoryService.getById(historyId);
            RentalStatus previousStatus = current.history().getRentalStatus();
            Item item = current.item();

            boolean isConsumable = requestedStatus == RentalStatus.CONFIRMED
                && item != null && item.getType() == ItemType.CONSUMABLE;
            RentalStatus newStatus = isConsumable ? RentalStatus.RETURNED : requestedStatus;

            RentalHistory history = adminRentalHistoryService.changeStatusByAdmin(historyId, newStatus, workerId);

            // 전후 상태의 재고 차지 여부를 비교하므로 끝난 상태끼리 바꿨다 되돌려도 재고가 어긋나지 않는다
            ItemType itemType = item == null ? null : item.getType();
            boolean heldBefore = previousStatus.holdsStock(itemType);
            boolean heldAfter = newStatus.holdsStock(itemType);
            if (heldBefore && !heldAfter) {
                item = itemService.increaseStock(history.getItemId(), history.getRentedCount());
            } else if (!heldBefore && heldAfter) {
                item = itemService.decreaseStock(history.getItemId(), history.getRentedCount());
            }

            Member renter = memberService.getMapByIds(List.of(history.getMemberId())).get(history.getMemberId());
            return AdminRentalHistoryResponse.from(new RentalRecord(history, item), renter);
        });
    }

    private void validateIsAdmin(Long workerId) {
        Member worker = memberService.getById(workerId);
        if (worker.getRole() != Role.ADMIN) {
            throw new BusinessException(RentalErrorCode.WORKER_NOT_ADMIN);
        }
    }
}
