package kr.ac.kookmin.stream.api.app.welfare.rental.usecase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import kr.ac.kookmin.stream.common.BusinessException;
import kr.ac.kookmin.stream.welfare.domain.fee.service.PayerService;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.RentalErrorCode;
import kr.ac.kookmin.stream.welfare.domain.rental.service.ItemService;
import kr.ac.kookmin.stream.welfare.domain.rental.service.RentalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 대여 신청. {@code fee}(회비 납부 확인)와 {@code rental}(재고 차감, 이력 생성) 두 도메인을 조합한다.
 * 대여 시간(영업시간·점심시간) 검증은 DB에 닿지 않으므로 이 트랜잭션에 들어오기 전, 컨트롤러에서 끝낸다.
 */
@Component
@RequiredArgsConstructor
public class RentalApplyUseCase {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final PayerService payerService;
    private final ItemService itemService;
    private final RentalHistoryService rentalHistoryService;

    @Transactional
    public void apply(Long memberId, Long itemId, int count, int rentAtHour, int rentAtMinute, boolean ignoreDuplicate) {
        // 동시성 보호(락) 없이 진행한다 — 재고 경쟁, 중복 대여 경쟁 둘 다 이론적으로 남아있는
        // 경합이다. 필요해지면 별도로 다시 도입한다(billilge-rental-apply-review-fixes.md 참고).
        if (!payerService.isPayer(memberId)) {
            throw new BusinessException(RentalErrorCode.MEMBER_IS_NOT_PAYER);
        }

        if (!ignoreDuplicate && rentalHistoryService.existsActiveRental(itemId, memberId)) {
            throw new BusinessException(RentalErrorCode.RENTAL_ITEM_DUPLICATED);
        }

        Item item = itemService.decreaseStock(itemId, count);

        LocalDateTime now = LocalDateTime.now(KST);
        LocalDateTime rentAt = LocalDate.now(KST).atTime(rentAtHour, rentAtMinute);
        rentalHistoryService.create(item, memberId, count, now, rentAt);
    }
}
