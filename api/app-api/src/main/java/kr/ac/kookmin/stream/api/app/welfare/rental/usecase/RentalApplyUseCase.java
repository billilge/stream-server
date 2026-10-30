package kr.ac.kookmin.stream.api.app.welfare.rental.usecase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.common.LockExecutor;
import kr.ac.kookmin.stream.internal.domain.config.service.AdminConfigValueService;
import kr.ac.kookmin.stream.welfare.domain.fee.service.PayerService;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalBlockPeriodConfigKeys;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalErrorCode;
import kr.ac.kookmin.stream.welfare.domain.rental.service.ItemService;
import kr.ac.kookmin.stream.welfare.domain.rental.service.RentalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** 대여 신청. {@code fee}(회비 납부 확인)·{@code rental}(재고 차감, 이력 생성)·{@code internal}(이용 제한 기간 조회) 도메인을 조합한다. */
@Component
@RequiredArgsConstructor
public class RentalApplyUseCase {

    private final LockExecutor lockExecutor;
    private final PayerService payerService;
    private final ItemService itemService;
    private final RentalHistoryService rentalHistoryService;
    private final AdminConfigValueService adminConfigValueService;

    public void apply(Long memberId, Long itemId, int count, int rentAtHour, int rentAtMinute, boolean ignoreDuplicate) {
        validateNotBlocked();

        lockExecutor.executeOptimistic(() -> {
            payerService.validatePayer(memberId);

            if (!ignoreDuplicate && rentalHistoryService.existsActiveRental(itemId, memberId)) {
                throw new BusinessException(RentalErrorCode.RENTAL_ITEM_DUPLICATED);
            }

            Item item = itemService.decreaseStock(itemId, count);

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime rentAt = LocalDate.now().atTime(rentAtHour, rentAtMinute);
            rentalHistoryService.create(item, memberId, null, count, now, rentAt);
        });
    }

    private void validateNotBlocked() {
        LocalDate startDate = adminConfigValueService.getValue(RentalBlockPeriodConfigKeys.RENTAL_BLOCK_START)
            .map(LocalDate::parse)
            .orElse(null);
        LocalDate endDate = adminConfigValueService.getValue(RentalBlockPeriodConfigKeys.RENTAL_BLOCK_END)
            .map(LocalDate::parse)
            .orElse(null);

        if (startDate == null || endDate == null) {
            return;
        }

        LocalDate today = LocalDate.now();
        if (!today.isBefore(startDate) && !today.isAfter(endDate)) {
            String reason = adminConfigValueService.getValue(RentalBlockPeriodConfigKeys.RENTAL_BLOCK_REASON).orElse("");
            throw new BusinessException(RentalErrorCode.INVALID_RENTAL_TIME_BLOCKED, reason);
        }
    }
}
