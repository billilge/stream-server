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
        // itemService.decreaseStock()이 이 트랜잭션의 첫 조회여야 한다 — 그 안의 비관적 락 조회가
        // 먼저 실행돼야, 뒤이은 평범한 조회(isPayer, existsActiveRental)들이 락 획득 이후(=
        // 경쟁 상대가 커밋한 이후) 시점의 데이터를 보게 된다(MySQL REPEATABLE READ 스냅샷).
        Item item = itemService.decreaseStock(itemId, count);

        if (!payerService.isPayer(memberId)) {
            throw new BusinessException(RentalErrorCode.MEMBER_IS_NOT_PAYER);
        }

        if (!ignoreDuplicate && rentalHistoryService.existsActiveRental(itemId, memberId)) {
            throw new BusinessException(RentalErrorCode.RENTAL_ITEM_DUPLICATED);
        }

        LocalDateTime now = LocalDateTime.now(KST);
        LocalDateTime rentAt = LocalDate.now(KST).atTime(rentAtHour, rentAtMinute);
        rentalHistoryService.create(item, memberId, count, now, rentAt);
    }
}
