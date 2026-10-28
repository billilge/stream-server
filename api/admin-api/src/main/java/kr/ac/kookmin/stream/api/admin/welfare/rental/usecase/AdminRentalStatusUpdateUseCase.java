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

/**
 * 관리자 대여 상태 변경. {@code rental}(상태 전이, 처리자 로그)과 {@code item}(재고 복구) 두 도메인을 조합한다.
 * <p>
 * 소모품은 CONFIRMED로 승인 요청이 와도 RENTAL을 거치지 않고 바로 RETURNED로 전이한다({@code RentalApplyUseCase}가
 * 신청 시점에 이미 재고를 차감해 둔 걸 그대로 "소비 완료"로 보는 것이라, 승인 즉시 종료 상태가 맞다). 이때는
 * 재고를 복구하지 않는다 — 그래서 재고 복구 여부는 "RETURNED/CANCEL/REJECTED로 새로 바뀌었는가"뿐 아니라
 * "RETURNED라면 대여품(RENTAL)인가"도 함께 봐야 한다. 재고 차감·복구가 걸리므로 {@code RentalApplyUseCase}와
 * 같은 이유로 {@code LockExecutor}로 감싼다.
 */
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

            boolean consumableAutoComplete = requestedStatus == RentalStatus.CONFIRMED
                && item != null && item.getType() == ItemType.CONSUMABLE;
            RentalStatus effectiveStatus = consumableAutoComplete ? RentalStatus.RETURNED : requestedStatus;

            RentalHistory history = adminRentalHistoryService.changeStatusByAdmin(historyId, effectiveStatus, workerId);

            if (shouldRestock(previousStatus, effectiveStatus, item)) {
                item = itemService.increaseStock(history.getItemId(), history.getRentedCount());
            }

            Member renter = memberService.findAllByIds(List.of(history.getMemberId())).stream()
                .findFirst()
                .orElse(null);
            return AdminRentalHistoryResponse.from(new RentalRecord(history, item), renter);
        });
    }

    private boolean shouldRestock(RentalStatus previousStatus, RentalStatus newStatus, Item item) {
        if (previousStatus.isFinished()) {
            return false;
        }
        if (newStatus.isCancelledOrRejected()) {
            return true;
        }
        return newStatus == RentalStatus.RETURNED && item != null && item.getType() == ItemType.RENTAL;
    }
}
